package facade.actors.queue;

import facade.actors.manager.Action;
import facade.actors.manager.CreateAction;
import facade.actors.manager.DeleteAction;
import facade.actors.manager.FindAction;
import facade.dto.CreateActorRequest;
import fj.Unit;
import fj.control.parallel.Actor;
import fj.control.parallel.Strategy;
import fj.function.Effect1;

import java.util.HashMap;
import java.util.Map;


public class ManagerEffect implements Effect1<Action> {
    private final Map<String, Actor<Message>> actors = new HashMap<>();
//        WeakHashMap<String, Actor<Message>> map = new WeakHashMap<String, Actor<Message>>();

    private final Strategy<Unit> strategy;
    private final Actor<Action> manager;

    public static ManagerEffect getInstance(Strategy<Unit> strategy, Actor<Action> manager) {
        return new ManagerEffect(strategy, manager);
    }

    private ManagerEffect(Strategy<Unit> strategy, Actor<Action> manager) {
        this.strategy = strategy;
        this.manager = manager;
    }

    @Override
    public void f(Action action) {
        if (action instanceof CreateAction) {
            CreateAction ac = (CreateAction) action;
            final Actor<Message> actor = Actor.queueActor(strategy, Effect.getInstance(manager, ac.getUuid()));
            actors.put(ac.getUuid(), actor);
            CreateActorRequest<Message> response = new CreateActorRequest<>(ac.getUuid(), actor);
            ac.getFuture().set(response);
        } else if (action instanceof FindAction) {
            FindAction ac = (FindAction) action;
            Actor<Message> actor = actors.get(ac.getUuid());
            if (actor != null) {
                ac.getFuture().set(actor);
            } else {
                ac.getFuture().setException(new RuntimeException("actor not found."));
            }
        } else if (action instanceof DeleteAction) {
            DeleteAction ac = (DeleteAction) action;
            actors.remove(ac.getUuid());
        }
    }
}
