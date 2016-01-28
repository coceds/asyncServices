package calculation.service;


import calculation.dto.CalculationResponse;
import org.springframework.web.context.request.async.DeferredResult;

public interface AsyncCalculationService {

    DeferredResult<CalculationResponse> multipleByTwo(Integer parameter);

    DeferredResult<CalculationResponse> multipleByThree(Integer parameter);
}
