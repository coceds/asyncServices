package facade.actors.manager;

import com.google.common.util.concurrent.SettableFuture;
import facade.actors.queue.Message;
import fj.control.parallel.Actor;

public class FindAction implements Action {

    private final String uuid;
    private final SettableFuture<Actor<Message>> future;

    public FindAction(String uuid, SettableFuture<Actor<Message>> future) {
        this.uuid = uuid;
        this.future = future;
    }

    public String getUuid() {
        return uuid;
    }

    public SettableFuture<Actor<Message>> getFuture() {
        return future;
    }
}
