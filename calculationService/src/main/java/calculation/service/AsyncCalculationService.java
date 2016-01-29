package calculation.service;


import calculation.dto.CalculationResponse;
import org.springframework.web.context.request.async.DeferredResult;

import java.math.BigDecimal;

public interface AsyncCalculationService {

    DeferredResult<CalculationResponse> multipleByTwo(BigDecimal parameter);

    DeferredResult<CalculationResponse> multipleByThree(BigDecimal parameter);
}
