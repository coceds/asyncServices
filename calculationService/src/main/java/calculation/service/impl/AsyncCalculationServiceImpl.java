package calculation.service.impl;


import calculation.service.AsyncCalculationService;
import calculation.service.CalculationService;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;

@Service
class AsyncCalculationServiceImpl implements AsyncCalculationService {

    private static final Logger logger = LoggerFactory.getLogger(AsyncCalculationServiceImpl.class);

    @Autowired
    private CalculationService calculationService;
    private ListeningExecutorService executorService;

    @Override
    public ListenableFuture<BigDecimal> multipleByTwo(BigDecimal parameter) {
        return executorService.submit(
                () -> calculationService.multipleByTwo(parameter)
        );
    }

    @Override
    public ListenableFuture<BigDecimal> multipleByThree(BigDecimal parameter) {
        return executorService.submit(
                () -> calculationService.multipleByThree(parameter)
        );
    }

    @Autowired
    public void setExecutorService(ExecutorService service) {
        executorService = MoreExecutors.listeningDecorator(service);
    }

}
