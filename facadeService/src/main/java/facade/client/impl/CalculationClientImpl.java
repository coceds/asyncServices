package facade.client.impl;

import facade.client.AsyncStreamClient;
import facade.client.CalculationClient;
import facade.client.CalculationEndPoints;
import facade.dto.CalculationRequest;
import facade.dto.CalculationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import rx.Observable;

@Service
class CalculationClientImpl implements CalculationClient {

    @Autowired
    private AsyncRestClientImpl asyncRestClient;
    @Autowired
    private AsyncStreamClient asyncStreamClient;

    @Override
    public ListenableFuture<ResponseEntity<CalculationResponse>> multipleByTwo(CalculationRequest request) {
        return asyncRestClient.exchange(CalculationEndPoints.MULTIPLY_BY_TWO, request);
    }

    @Override
    public ListenableFuture<ResponseEntity<CalculationResponse>> multipleByThree(CalculationRequest request) {
        return asyncRestClient.exchange(CalculationEndPoints.MULTIPLY_BY_THREE, request);
    }

    @Override
    public Observable<CalculationResponse> randomStreamBigDecimal(CalculationRequest request) {
        return asyncStreamClient.createPost(CalculationEndPoints.RANDOM_STREAM, request);
    }

    @Override
    public Observable<CalculationResponse> randomStreamBoolean() {
        return asyncStreamClient.createPost(CalculationEndPoints.RANDOM_STREAM_BOOLEAN, null);
    }
}
