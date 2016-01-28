package calculation.service.impl;


import calculation.dto.CalculationResponse;
import calculation.service.AsyncCalculationService;
import calculation.service.CalculationService;
import com.google.common.util.concurrent.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.ExecutorService;

@Service
public class AsyncCalculationServiceImpl implements AsyncCalculationService {

    @Autowired
    private CalculationService calculationService;
    private ListeningExecutorService service;

    public DeferredResult<CalculationResponse> multipleByTwo(Integer parameter) {
        final DeferredResult<CalculationResponse> result = new DeferredResult<>(5000);
        ListenableFuture<Integer> future = service.submit(
                () -> calculationService.multipleByTwo(parameter)
        );
        Futures.addCallback(future, new FutureCallback<Integer>() {
            @Override
            public void onSuccess(Integer res) {
                result.setResult(new CalculationResponse(res));
            }

            @Override
            public void onFailure(Throwable throwable) {
                result.setResult(new CalculationResponse("error."));
            }
        });
        return result;
    }

    public DeferredResult<CalculationResponse> multipleByThree(Integer parameter) {
        final DeferredResult<CalculationResponse> result = new DeferredResult<>(5000);
        ListenableFuture<Integer> future = service.submit(
                () -> calculationService.multipleByThree(parameter)
        );
        Futures.addCallback(future, new FutureCallback<Integer>() {
            @Override
            public void onSuccess(Integer res) {
                result.setResult(new CalculationResponse(res));
            }

            @Override
            public void onFailure(Throwable throwable) {
                result.setResult(new CalculationResponse("error."));
            }
        });
        return result;
    }

    @Autowired
    public void setExecutorService(ExecutorService executorService) {
        this.service = MoreExecutors.listeningDecorator(executorService);
    }
}
