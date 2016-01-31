package facade.client;

import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;

public interface CalculationClient {

    String SERVICE_PATH = "/calculationService";
    String MULTIPLY_BY_TWO = SERVICE_PATH + "/multipleByTwo";
    String MULTIPLY_BY_THREE = SERVICE_PATH + "/multipleByThree";

    <T, K> ListenableFuture<ResponseEntity<T>> multipleByTwo(K request);

    <T, K> ListenableFuture<ResponseEntity<T>> multipleByThree(K request);
}
