package facade.actors.manager;

import com.google.common.util.concurrent.SettableFuture;
import facade.actors.queue.Message;
import facade.dto.CreateActorRequest;

public class CreateAction implements Action {

    private final SettableFuture<CreateActorRequest<Message>> future;
    private final String uuid;

    public CreateAction(SettableFuture<CreateActorRequest<Message>> future, String uuid) {
        this.future = future;
        this.uuid = uuid;
    }

    public SettableFuture<CreateActorRequest<Message>> getFuture() {
        return future;
    }

    public String getUuid() {
        return uuid;
    }
}
