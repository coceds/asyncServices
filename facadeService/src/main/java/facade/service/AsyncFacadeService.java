package facade.service;

import com.google.common.util.concurrent.ListenableFuture;
import facade.dto.CalculationResponse;
import rx.Observable;

import java.math.BigDecimal;

public interface AsyncFacadeService {

    ListenableFuture<BigDecimal> calculate(BigDecimal parameter);

    Observable<CalculationResponse> randomStream(BigDecimal parameter);

    Integer randomStreamWithActors(final BigDecimal parameter);

    ListenableFuture<CalculationResponse> getNextById(Integer id);
}
