package facade.actors.queue;


import com.google.common.util.concurrent.SettableFuture;
import facade.actors.manager.Action;
import facade.actors.manager.DeleteAction;
import facade.dto.CalculationResponse;
import facade.dto.ReadResponse;
import fj.control.parallel.Actor;
import fj.function.Effect1;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.Queue;

public class Effect implements Effect1<Message> {

    private boolean finish = false;
    private final String uuid;
    private final Queue<CalculationResponse> elements = new LinkedList<>();
    private final Queue<SettableFuture<ReadResponse>> futures = new LinkedList<>();
    private Throwable exception;
    private final Actor<Action> parent;

    public static Effect getInstance(Actor<Action> parent, String uuid) {
        return new Effect(parent, uuid);
    }

    private Effect(Actor<Action> parent, String uuid) {
        this.parent = parent;
        this.uuid = uuid;
    }

    @Override
    public void f(Message message) {
        if (message instanceof FinishMessage) {
            finish = true;
            if (CollectionUtils.isEmpty(elements)) {
                while (!CollectionUtils.isEmpty(futures)) {
                    SettableFuture<ReadResponse> future = futures.poll();
                    future.setException(new RuntimeException("no more elements."));
                }
                parent.act(new DeleteAction(uuid));
            }
        } else if (message instanceof NextMessage) {
            NextMessage m = (NextMessage) message;
            if (CollectionUtils.isEmpty(futures)) {
                elements.add(m.getResponse());
            } else {
                ReadResponse response = new ReadResponse(m.getResponse());
                if (!finish) {
                    response.setNext(true);
                }
                futures.poll().set(response);
            }

        } else if (message instanceof ReadMessage) {
            ReadMessage m = (ReadMessage) message;
            if (exception != null) {
                m.getFuture().setException(exception);
                return;
            }
            if (finish && CollectionUtils.isEmpty(elements)) {
                parent.act(new DeleteAction(uuid));
                System.out.println("self delete");
                m.getFuture().setException(new RuntimeException("queue is closed"));
            } else if (elements.size() > 0) {
                ReadResponse response = new ReadResponse(elements.poll());

                if (!finish || elements.size() > 0) {
                    response.setNext(true);
                }
                m.getFuture().set(response);
            } else {
                futures.add(m.getFuture());
            }
        } else if (message instanceof ExceptionMessage) {
            finish = true;
            exception = ((ExceptionMessage) message).getException();
        }
    }
}
