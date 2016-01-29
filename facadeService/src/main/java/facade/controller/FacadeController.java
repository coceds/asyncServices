package facade.controller;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import facade.dto.CalculationRequest;
import facade.dto.CalculationResponse;
import facade.service.AsyncFacadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class FacadeController {

    @Autowired
    private AsyncFacadeService asyncFacadeService;

    @RequestMapping(value = "/calculate", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public DeferredResult<CalculationResponse> calculate(@RequestBody CalculationRequest request) {
        final ListenableFuture<List<ResponseEntity<CalculationResponse>>> future =
                asyncFacadeService.calculate(request.getParameter());
        final DeferredResult<CalculationResponse> result = new DeferredResult<>();
        Futures.addCallback(future, new FutureCallback<List<ResponseEntity<CalculationResponse>>>() {
            @Override
            public void onSuccess(List<ResponseEntity<CalculationResponse>> responseEntities) {
                BigDecimal res = BigDecimal.ZERO;
                for (ResponseEntity<CalculationResponse> entity : responseEntities) {
                    if (entity != null && entity.getBody().getResult() != null) {
                        res = res.add(entity.getBody().getResult());
                    } else {
                        result.setResult(new CalculationResponse("error"));
                        break;
                    }
                }
                result.setResult(new CalculationResponse(res));
            }

            @Override
            public void onFailure(Throwable throwable) {
                result.setResult(new CalculationResponse("error"));
            }
        });
        return result;
    }
}
