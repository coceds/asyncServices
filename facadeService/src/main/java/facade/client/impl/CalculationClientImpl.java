package facade.client.impl;

import facade.client.CalculationClient;
import facade.client.CalculationEndPoints;
import facade.dto.CalculationRequest;
import facade.dto.CalculationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

@Service
class CalculationClientImpl implements CalculationClient {

    @Autowired
    private AsyncRestClientImpl asyncRestClient;

    @Override
    public ListenableFuture<ResponseEntity<CalculationResponse>> multipleByTwo(CalculationRequest request) {
        return asyncRestClient.exchange(CalculationEndPoints.MULTIPLY_BY_TWO, request);
    }

    @Override
    public ListenableFuture<ResponseEntity<CalculationResponse>> multipleByThree(CalculationRequest request) {
        return asyncRestClient.exchange(CalculationEndPoints.MULTIPLY_BY_THREE, request);
    }
}
