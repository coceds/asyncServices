package calculation.service;


import calculation.dto.CalculationResponse;
import rx.Observable;

import java.math.BigDecimal;

public interface ObservableService {

    Observable<CalculationResponse> getRandomStream(BigDecimal max);

    Observable<CalculationResponse> getRandomStreamBoolean();
}
