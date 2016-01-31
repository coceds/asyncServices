package facade.utils;

import com.google.common.util.concurrent.ListenableFuture;

public final class ListenableUtils {

    private ListenableUtils() {
    }

    public static <T> ListenableFuture<T> springListenableFutureToGuava(
            org.springframework.util.concurrent.ListenableFuture<T> listenableFuture) {
        return new GuavaListenableWrappingSpringListenableFuture<>(listenableFuture);
    }
}
