package facade.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.*;
import facade.client.CalculationClient;
import facade.dto.CalculationRequest;
import facade.dto.CalculationResponse;
import facade.service.AsyncFacadeService;
import facade.service.Calculation;
import facade.utils.ListenableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import rx.Observable;

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

    private ListeningExecutorService executorService;
    private ObjectMapper mapper = new ObjectMapper();
    @Value("#{properties.calculationServiceUrl}")
    private String baseUrl;


    @Override
    public Observable<CalculationResponse> randomStream(BigDecimal parameter) {
        Observable<CalculationResponse> one = calculationClient.randomStreamBigDecimal(new CalculationRequest(parameter));
        Observable<CalculationResponse> two = calculationClient.randomStreamBoolean();
        Observable<CalculationResponse> observable = Observable.zip(one, two, (calculationResponse, calculationResponse2) -> {
            calculationResponse2.setResult(calculationResponse.getResult());
            return calculationResponse2;
        }).filter(calculationResponse -> calculationResponse.isFlag());
        return observable;
    }

    @Override
    public ListenableFuture<BigDecimal> calculate(BigDecimal parameter) {

        List<ListenableFuture<ResponseEntity<CalculationResponse>>> futures = new ArrayList<>();

        futures.add(ListenableUtils.convertFuture(
                calculationClient.multipleByTwo(new CalculationRequest(parameter))));

        futures.add(ListenableUtils.convertFuture(
                calculationClient.multipleByThree(new CalculationRequest(parameter))));


        ListenableFuture<List<ResponseEntity<CalculationResponse>>> successfulQueries = Futures.allAsList(futures);

        AsyncFunction<List<ResponseEntity<CalculationResponse>>, BigDecimal> queryFunction =
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
                            calculation.calculate(ImmutableList.copyOf(results))
                    );
                };

        return Futures.transform(successfulQueries, queryFunction);
    }

    @Autowired
    public void setExecutorService(ExecutorService service) {
        executorService = MoreExecutors.listeningDecorator(service);
    }
}
