package calculation.controller;

import calculation.dto.CalculationRequest;
import calculation.dto.CalculationResponse;
import calculation.service.AsyncCalculationService;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.math.BigDecimal;

@RestController
public class CalculationController {

    @Autowired
    private AsyncCalculationService asyncCalculationService;

    @RequestMapping(value = "/multipleByTwo", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public DeferredResult<CalculationResponse> multipleByTwo(@RequestBody CalculationRequest request) {
        ListenableFuture<BigDecimal> response = asyncCalculationService.multipleByTwo(request.getParameter());
        return setResult(response);
    }

    @RequestMapping(value = "/multipleByThree", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public DeferredResult<CalculationResponse> multipleByThree(@RequestBody CalculationRequest request) {
        ListenableFuture<BigDecimal> response = asyncCalculationService.multipleByThree(request.getParameter());
        return setResult(response);
    }

    private DeferredResult<CalculationResponse> setResult(ListenableFuture<BigDecimal> response) {
        final DeferredResult<CalculationResponse> result = new DeferredResult<>();
        Futures.addCallback(response, new FutureCallback<BigDecimal>() {
            @Override
            public void onSuccess(BigDecimal res) {
                if (res == null) {
                    result.setResult(new CalculationResponse("error"));
                } else {
                    result.setResult(new CalculationResponse(res));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                result.setResult(new CalculationResponse("error"));
            }
        });
        return result;
    }
}
