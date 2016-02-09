package facade.actor;


import com.google.common.util.concurrent.SettableFuture;
import facade.dto.CalculationResponse;
import fj.function.Effect1;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.Queue;

public class Effect implements Effect1<Message> {

    private boolean finish = false;
    private final Queue<CalculationResponse> elements = new LinkedList<>();
    private final Queue<SettableFuture<CalculationResponse>> futures = new LinkedList<>();
    private Throwable exception;

    public static Effect getInstance() {
        return new Effect();
    }

    private Effect() {
    }

    @Override
    public void f(Message message) {
        if (message instanceof FinishMessage) {
            finish = true;
            if (CollectionUtils.isEmpty(elements)) {
                while (!CollectionUtils.isEmpty(futures)) {
                    SettableFuture<CalculationResponse> future = futures.poll();
                    future.setException(new RuntimeException("no more elements."));
                }
            }
        } else if (message instanceof NextMessage) {
            NextMessage m = (NextMessage) message;
            if (CollectionUtils.isEmpty(futures)) {
                elements.add(m.getResponse());
            } else {
                futures.poll().set(m.getResponse());
            }

        } else if (message instanceof ReadMessage) {
            ReadMessage m = (ReadMessage) message;
            if (exception != null) {
                m.getFuture().getFuture().setException(exception);
                return;
            }
            if (finish && CollectionUtils.isEmpty(elements)) {
                m.getFuture().getFuture().setException(new RuntimeException("queue is closed"));
            } else if (elements.size() > 0) {
                m.getFuture().getFuture().set(elements.poll());
                if (!finish || elements.size() > 0) {
                    m.getFuture().setNext(true);
                }
            } else {
                futures.add(m.getFuture().getFuture());
                if (!finish) {
                    m.getFuture().setNext(true);
                }
            }
        } else if (message instanceof ExceptionMessage) {
            finish = true;
            exception = ((ExceptionMessage) message).getException();
        }
    }
}
