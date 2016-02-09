package facade.dto;

import fj.control.parallel.Actor;

public class CreateActorRequest<T> {

    private final String uuid;
    private final Actor<T> actor;

    public CreateActorRequest(String uuid, Actor<T> actor) {
        this.uuid = uuid;
        this.actor = actor;
    }

    public String getUuid() {
        return uuid;
    }

    public Actor<T> getActor() {
        return actor;
    }
}
