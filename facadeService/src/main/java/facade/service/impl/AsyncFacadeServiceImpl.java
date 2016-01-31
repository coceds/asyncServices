package facade.service.impl;

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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

    public ListenableFuture<CalculationResponse> calculate(BigDecimal parameter) {

        List<ListenableFuture<ResponseEntity<CalculationResponse>>> futures = new ArrayList<>();
        final org.springframework.util.concurrent.ListenableFuture<ResponseEntity<CalculationResponse>> futureTwo =
                calculationClient.multipleByTwo(new CalculationRequest(parameter));
        futures.add(ListenableUtils.springListenableFutureToGuava(futureTwo));

        org.springframework.util.concurrent.ListenableFuture<ResponseEntity<CalculationResponse>> futureThree =
                calculationClient.multipleByThree(new CalculationRequest(parameter));
        futures.add(ListenableUtils.springListenableFutureToGuava(futureThree));

        ListenableFuture<List<ResponseEntity<CalculationResponse>>> successfulQueries = Futures.allAsList(futures);

        AsyncFunction<List<ResponseEntity<CalculationResponse>>, CalculationResponse> queryFunction =
                new AsyncFunction<List<ResponseEntity<CalculationResponse>>, CalculationResponse>() {
                    public ListenableFuture<CalculationResponse> apply(List<ResponseEntity<CalculationResponse>> entities) {
                        final List<BigDecimal> results = entities.stream().map(e -> {
                            if (e == null || e.getBody().getResult() == null) {
                                throw new RuntimeException("calculation failed.");
                            } else {
                                return e.getBody().getResult();
                            }
                        }).collect(Collectors.toList());
                        return executorService.submit(() ->
                                        new CalculationResponse(calculation.calculate(results))
                        );
                    }
                };
        return Futures.transform(successfulQueries, queryFunction);
    }

    @Autowired
    public void setExecutorService(ExecutorService service) {
        executorService = MoreExecutors.listeningDecorator(service);
    }
}
