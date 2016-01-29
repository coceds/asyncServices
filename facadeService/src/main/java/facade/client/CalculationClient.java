package facade.client;

import facade.dto.CalculationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;

import java.math.BigDecimal;

public interface CalculationClient {

    String SERVICE_PATH = "/calculationService";
    String MULTIPLY_BY_TWO = SERVICE_PATH + "/multipleByTwo";
    String MULTIPLY_BY_THREE = SERVICE_PATH + "/multipleByThree";

    ListenableFuture<ResponseEntity<CalculationResponse>> multipleByTwo(BigDecimal parameter);

    ListenableFuture<ResponseEntity<CalculationResponse>> multipleByThree(BigDecimal parameter);
}
