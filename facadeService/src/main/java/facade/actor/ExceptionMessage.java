package facade.actor;

public class ExceptionMessage implements Message {

    private Throwable exception;

    public ExceptionMessage(Throwable exception) {
        this.exception = exception;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }
}
