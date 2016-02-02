package facade.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.*;
import facade.client.CalculationClient;
import facade.dto.CalculationRequest;
import facade.dto.CalculationResponse;
import facade.service.AsyncFacadeService;
import facade.service.Calculation;
import facade.utils.ListenableUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import rx.Observable;
import rx.apache.http.ObservableHttp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Service
class AsyncFacadeServiceImpl implements AsyncFacadeService {

    private static final Logger logger = LoggerFactory.getLogger(AsyncFacadeServiceImpl.class);

    @Autowired
    private CalculationClient calculationClient;
    @Autowired
    private Calculation calculation;
    @Autowired
    private CloseableHttpAsyncClient asyncClient;

    private ListeningExecutorService executorService;
    private ObjectMapper mapper = new ObjectMapper();
    @Value("#{properties.calculationServiceUrl}")
    private String baseUrl;


    @Override
    public Observable<CalculationResponse> randomStream(BigDecimal parameter) {
        Observable<CalculationResponse> observable;
        try {

            String content = mapper.writeValueAsString(new CalculationRequest(parameter));
            Observable<CalculationResponse> one = processPost(content, "/calculationService/randomStream");
            Observable<CalculationResponse> two = processPost("", "/calculationService/randomStreamBoolean");
            observable = Observable.zip(one, two, (calculationResponse, calculationResponse2) -> {
                if (!calculationResponse2.isFlag()) {
                    return calculationResponse2;
                } else {
                    calculationResponse.setFlag(true);
                    return calculationResponse;
                }
            });
        } catch (UnsupportedEncodingException | JsonProcessingException e) {
            logger.error("error", e);
            throw new RuntimeException(e);
        }
        return observable;
    }

    private Observable<CalculationResponse> processPost(String content, String url) throws UnsupportedEncodingException {
        TypeReference<CalculationResponse> typeReference = new TypeReference<CalculationResponse>() {
        };
        return ObservableHttp.createRequest(
                HttpAsyncMethods.createPost(baseUrl + url, content,
                        ContentType.APPLICATION_JSON), asyncClient)
                .toObservable()
                .flatMap(observableHttpResponse ->
                        observableHttpResponse.getContent().map(bb ->
                                {
                                    try {
                                        String value = new String(bb);
                                        value = value.replace("data:", "");
                                        logger.info(value);
                                        return mapper.readValue(value, typeReference);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                        )
                );
    }

    @Override
    public ListenableFuture<CalculationResponse> calculate(BigDecimal parameter) {

        List<ListenableFuture<ResponseEntity<CalculationResponse>>> futures = new ArrayList<>();

        futures.add(ListenableUtils.convertFuture(
                calculationClient.multipleByTwo(new CalculationRequest(parameter))));

        futures.add(ListenableUtils.convertFuture(
                calculationClient.multipleByThree(new CalculationRequest(parameter))));


        ListenableFuture<List<ResponseEntity<CalculationResponse>>> successfulQueries = Futures.allAsList(futures);

        AsyncFunction<List<ResponseEntity<CalculationResponse>>, CalculationResponse> queryFunction =
                (List<ResponseEntity<CalculationResponse>> entities) -> {
                    final List<BigDecimal> results = entities.stream().map(e -> {
                        if (e.getBody().getResult() == null) {
                            logger.error("calculation failed.");
                            throw new RuntimeException("calculation failed.");
                        } else {
                            return e.getBody().getResult();
                        }
                    }).collect(Collectors.toList());
                    return executorService.submit(() ->
                            new CalculationResponse(calculation.calculate(ImmutableList.copyOf(results)))
                    );
                };

        return Futures.transform(successfulQueries, queryFunction);
    }

    @Autowired
    public void setExecutorService(ExecutorService service) {
        executorService = MoreExecutors.listeningDecorator(service);
    }
}
