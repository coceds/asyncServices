package facade.utils;

import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class GuavaListenableWrappingSpringListenableFuture<T> implements com.google.common.util.concurrent.ListenableFuture<T> {
    private ListenableFuture<T> springFuture;

    public GuavaListenableWrappingSpringListenableFuture(ListenableFuture<T> springListenableFuture) {
        this.springFuture = springListenableFuture;
    }

    @Override
    public void addListener(final Runnable command, final Executor executor) {
        springFuture.addCallback(new ListenableFutureCallback<T>() {
            @Override
            public void onSuccess(T result) {
                executor.execute(command);
            }

            @Override
            public void onFailure(Throwable t) {
                executor.execute(command);
            }
        });
    }

    @Override
    public boolean isDone() {
        return springFuture.isDone();
    }

    @Override
    public boolean isCancelled() {
        return springFuture.isCancelled();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return springFuture.cancel(mayInterruptIfRunning);
    }

    @Override
    public T get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return springFuture.get(timeout, unit);
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return springFuture.get();
    }
}
