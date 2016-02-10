package facade.service;

import com.google.common.util.concurrent.ListenableFuture;
import facade.dto.CalculationResponse;
import facade.dto.ReadResponse;
import rx.Observable;

import java.math.BigDecimal;

public interface AsyncFacadeService {

    ListenableFuture<BigDecimal> calculate(BigDecimal parameter);

    Observable<CalculationResponse> randomStream(BigDecimal parameter);

    String randomStreamWithActors(final BigDecimal parameter);

    ListenableFuture<ReadResponse> getNextById(String uuid);

    ListenableFuture<ReadResponse> getNextByIdWithManager(String uuid);

    String randomStreamWithManagerActors(final BigDecimal parameter);
}
