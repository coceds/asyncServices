package facade.actors.manager;

import facade.actors.queue.Message;
import fj.control.parallel.Actor;

public class CreateAction implements Action {

    private final Actor<Message> actor;
    private final String uuid;

    public CreateAction(Actor<Message> actor, String uuid) {
        this.actor = actor;
        this.uuid = uuid;
    }

    public Actor<Message> getActor() {
        return actor;
    }

    public String getUuid() {
        return uuid;
    }
}
