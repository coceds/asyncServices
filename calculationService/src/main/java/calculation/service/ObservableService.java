package calculation.service;


import calculation.dto.CalculationResponse;
import rx.Observable;

import java.math.BigDecimal;

public interface ObservableService {

    Observable<BigDecimal> getRandomStream(BigDecimal max);

    Observable<Boolean> getRandomStreamBoolean();
}
