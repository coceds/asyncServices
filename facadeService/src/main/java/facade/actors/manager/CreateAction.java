package facade.actors.manager;

import com.google.common.util.concurrent.SettableFuture;
import facade.actors.queue.Message;
import facade.dto.CreateActorRequest;

public class CreateAction implements Action {

    private final SettableFuture<CreateActorRequest<Message>> future;

    public CreateAction(SettableFuture<CreateActorRequest<Message>> future) {
        this.future = future;
    }

    public SettableFuture<CreateActorRequest<Message>> getFuture() {
        return future;
    }
}
