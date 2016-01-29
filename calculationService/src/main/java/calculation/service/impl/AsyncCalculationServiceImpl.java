package calculation.service.impl;


import calculation.dto.CalculationResponse;
import calculation.service.AsyncCalculationService;
import calculation.service.CalculationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.context.request.async.DeferredResult;

import java.math.BigDecimal;

@Service
class AsyncCalculationServiceImpl implements AsyncCalculationService {

    private static final Logger logger = LoggerFactory.getLogger(AsyncCalculationServiceImpl.class);

    @Autowired
    private CalculationService calculationService;
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Override
    public DeferredResult<CalculationResponse> multipleByTwo(BigDecimal parameter) {
        final DeferredResult<CalculationResponse> result = new DeferredResult<>();
        ListenableFuture<BigDecimal> task = taskExecutor.submitListenable(
                () -> calculationService.multipleByTwo(parameter)
        );
        task.addCallback(new ListenableFutureCallback<BigDecimal>() {
            @Override
            public void onSuccess(BigDecimal res) {
                result.setResult(new CalculationResponse(res));
            }

            @Override
            public void onFailure(Throwable t) {
                logger.error("Error executing callback.", t);
                result.setResult(new CalculationResponse("error"));
            }
        });
        return result;
    }

    @Override
    public DeferredResult<CalculationResponse> multipleByThree(BigDecimal parameter) {
        final DeferredResult<CalculationResponse> result = new DeferredResult<>();
        ListenableFuture<BigDecimal> task = taskExecutor.submitListenable(
                () -> calculationService.multipleByThree(parameter)
        );
        task.addCallback(new ListenableFutureCallback<BigDecimal>() {
            @Override
            public void onSuccess(BigDecimal res) {
                result.setResult(new CalculationResponse(res));
            }

            @Override
            public void onFailure(Throwable t) {
                logger.error("Error executing callback.", t);
                result.setResult(new CalculationResponse("error"));
            }
        });
        return result;
    }

    private void setErrorToDeferredResult(DeferredResult<CalculationResponse> deferredResult, String error) {
        deferredResult.setResult(new CalculationResponse(error));
    }

}
