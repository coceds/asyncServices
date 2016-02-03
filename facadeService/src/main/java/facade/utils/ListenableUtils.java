package facade.utils;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import facade.dto.CalculationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFutureCallback;

public final class ListenableUtils {

    private ListenableUtils() {
    }

    public static <T> ListenableFuture<T> convertFuture(
            org.springframework.util.concurrent.ListenableFuture<T> listenableFuture) {

        final SettableFuture<T> settableFutureTwo = SettableFuture.create();
        listenableFuture.addCallback(new ListenableFutureCallback<T>() {
            @Override
            public void onSuccess(T calculationResponseResponseEntity) {
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
