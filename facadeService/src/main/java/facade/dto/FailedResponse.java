package facade.dto;

public class FailedResponse {

    private String message;
    private Exception exception;

    public FailedResponse() {
    }

    public FailedResponse(Exception exception, String message) {
        this.exception = exception;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
