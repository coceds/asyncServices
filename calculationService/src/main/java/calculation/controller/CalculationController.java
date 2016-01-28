package calculation.controller;

import calculation.dto.CalculationRequest;
import calculation.dto.CalculationResponse;
import calculation.service.AsyncCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.ExecutorService;

@RestController
public class CalculationController {

    @Autowired
    private AsyncCalculationService asyncCalculationService;
    @Autowired
    private ExecutorService executorService;

    @RequestMapping(value = "/multipleByTwo", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public DeferredResult<CalculationResponse> multipleByTwo(@RequestBody CalculationRequest request) {
        return asyncCalculationService.multipleByTwo(request.getParameter());
    }

    @RequestMapping(value = "/multipleByThree", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public DeferredResult<CalculationResponse> multipleByThree(@RequestBody CalculationRequest request) {
        return asyncCalculationService.multipleByThree(request.getParameter());
    }
}
