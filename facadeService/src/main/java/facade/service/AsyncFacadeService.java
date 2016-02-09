package facade.service;

import com.google.common.util.concurrent.ListenableFuture;
import facade.dto.CalculationResponse;
import facade.dto.FutureResponse;
import rx.Observable;

import java.math.BigDecimal;

public interface AsyncFacadeService {

    ListenableFuture<BigDecimal> calculate(BigDecimal parameter);

    Observable<CalculationResponse> randomStream(BigDecimal parameter);

    String randomStreamWithActors(final BigDecimal parameter);

    FutureResponse<CalculationResponse> getNextById(String uuid);
}
