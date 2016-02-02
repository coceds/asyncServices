package facade.service;

import com.google.common.util.concurrent.ListenableFuture;
import facade.dto.CalculationResponse;
import rx.Observable;

import java.math.BigDecimal;

public interface AsyncFacadeService {

    ListenableFuture<CalculationResponse> calculate(BigDecimal parameter);

    Observable<String> randomStream(BigDecimal parameter);
}
