package facade.client;

import facade.dto.CalculationRequest;
import facade.dto.CalculationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;

public interface CalculationClient {

    String SERVICE_PATH = "/calculationService";
    String MULTIPLY_BY_TWO = SERVICE_PATH + "/multipleByTwo";
    String MULTIPLY_BY_THREE = SERVICE_PATH + "/multipleByThree";
    String RANDOM_STREAM = SERVICE_PATH + "/randomStream";

    ListenableFuture<ResponseEntity<CalculationResponse>> multipleByTwo(CalculationRequest request);

    ListenableFuture<ResponseEntity<CalculationResponse>> multipleByThree(CalculationRequest request);

    ListenableFuture<ResponseEntity<CalculationResponse>> randomStream(CalculationRequest request);

}
