package facade.service;

import com.google.common.util.concurrent.ListenableFuture;
import facade.dto.CalculationResponse;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

public interface AsyncFacadeService {

    ListenableFuture<List<ResponseEntity<CalculationResponse>>> calculate(BigDecimal parameter);
}
