package facade.actors.queue;


import com.google.common.util.concurrent.SettableFuture;
import facade.dto.ReadResponse;

public class ReadMessage implements Message {

    private final SettableFuture<ReadResponse> future;

    public ReadMessage(SettableFuture<ReadResponse> future) {
        this.future = future;
    }

    public SettableFuture<ReadResponse> getFuture() {
        return future;
    }
}
