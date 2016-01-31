package facade.controller;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import facade.dto.CalculationRequest;
import facade.dto.CalculationResponse;
import facade.service.AsyncFacadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
public class FacadeController {

    @Autowired
    private AsyncFacadeService asyncFacadeService;

    @RequestMapping(value = "/calculate", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public DeferredResult<CalculationResponse> calculate(@RequestBody CalculationRequest request) {
        final ListenableFuture<CalculationResponse> future =
                asyncFacadeService.calculate(request.getParameter());
        return setResult(future);
    }

    private DeferredResult<CalculationResponse> setResult(ListenableFuture<CalculationResponse> response) {
        final DeferredResult<CalculationResponse> result = new DeferredResult<>();
        Futures.addCallback(response, new FutureCallback<CalculationResponse>() {
            @Override
            public void onSuccess(CalculationResponse response) {
                if (response.getResult() == null) {
                    result.setResult(new CalculationResponse("error"));
                } else {
                    result.setResult(response);
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                result.setResult(new CalculationResponse("error"));
            }
        });
        return result;
    }
}
