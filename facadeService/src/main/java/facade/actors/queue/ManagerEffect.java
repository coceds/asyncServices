package facade.actors.queue;

import facade.actors.manager.Action;
import facade.actors.manager.CreateAction;
import facade.actors.manager.DeleteAction;
import facade.actors.manager.FindAction;
import fj.control.parallel.Actor;
import fj.function.Effect1;

import java.util.HashMap;
import java.util.Map;


public class ManagerEffect implements Effect1<Action> {
    private final Map<String, Actor<Message>> actors = new HashMap<>();
//        WeakHashMap<String, Actor<Message>> map = new WeakHashMap<String, Actor<Message>>();

    public static ManagerEffect getInstance() {
        return new ManagerEffect();
    }

    private ManagerEffect() {

    }

    @Override
    public void f(Action action) {
        if (action instanceof CreateAction) {
            CreateAction ac = (CreateAction) action;
            actors.put(ac.getUuid(), ac.getActor());
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
