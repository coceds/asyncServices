package facade.utils;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import facade.dto.CalculationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFutureCallback;

public final class ListenableUtils {

    private ListenableUtils() {
    }

    public static ListenableFuture<ResponseEntity<CalculationResponse>> convertFuture(
            org.springframework.util.concurrent.ListenableFuture<ResponseEntity<CalculationResponse>> listenableFuture) {

        final SettableFuture<ResponseEntity<CalculationResponse>> settableFutureTwo = SettableFuture.create();
        listenableFuture.addCallback(new ListenableFutureCallback<ResponseEntity<CalculationResponse>>() {
            @Override
            public void onSuccess(ResponseEntity<CalculationResponse> calculationResponseResponseEntity) {
                settableFutureTwo.set(calculationResponseResponseEntity);
            }

            @Override
            public void onFailure(Throwable throwable) {
                settableFutureTwo.setException(throwable);
            }
        });
        return settableFutureTwo;
    }
}
