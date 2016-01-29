package facade.service.impl;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import facade.client.CalculationClient;
import facade.dto.CalculationResponse;
import facade.service.AsyncFacadeService;
import facade.utils.GuavaListenableWrappingSpringListenableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
class AsyncFacadeServiceImpl implements AsyncFacadeService {

    private static final Logger logger = LoggerFactory.getLogger(AsyncFacadeServiceImpl.class);

    @Autowired
    private CalculationClient calculationClient;

    public ListenableFuture<List<ResponseEntity<CalculationResponse>>> calculate(BigDecimal parameter) {

        List<ListenableFuture<ResponseEntity<CalculationResponse>>> futures = new ArrayList<>();
        org.springframework.util.concurrent.ListenableFuture<ResponseEntity<CalculationResponse>> futureTwo =
                calculationClient.multipleByTwo(parameter);
        ListenableFuture<ResponseEntity<CalculationResponse>> listenableTwo = new GuavaListenableWrappingSpringListenableFuture<>(futureTwo);
        futures.add(listenableTwo);
        org.springframework.util.concurrent.ListenableFuture<ResponseEntity<CalculationResponse>> futureThree =
                calculationClient.multipleByThree(parameter);
        ListenableFuture<ResponseEntity<CalculationResponse>> listenableThree = new GuavaListenableWrappingSpringListenableFuture<>(futureThree);
        futures.add(listenableThree);
        ListenableFuture<List<ResponseEntity<CalculationResponse>>> successfulQueries = Futures.allAsList(futures);

        return successfulQueries;
    }
}
