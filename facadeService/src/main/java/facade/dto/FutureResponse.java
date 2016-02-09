package facade.dto;


import com.google.common.util.concurrent.SettableFuture;

public class FutureResponse<T> {

    private final SettableFuture<T> future;
    private volatile boolean next;

    public FutureResponse(SettableFuture<T> future) {
        this.future = future;
    }

    public boolean isNext() {
        return next;
    }

    public void setNext(boolean next) {
        this.next = next;
    }

    public SettableFuture<T> getFuture() {
        return future;
    }

}
