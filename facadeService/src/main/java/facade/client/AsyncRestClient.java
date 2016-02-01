package facade.client;

import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;

public interface AsyncRestClient {

    <T> ListenableFuture<ResponseEntity<T>> exchange(CalculationEndPoints endPoint, Object request);
}
