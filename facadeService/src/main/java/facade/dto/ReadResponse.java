package facade.dto;


public class ReadResponse {

    private boolean next;
    private CalculationResponse response;

    public ReadResponse(CalculationResponse response) {
        this.response = response;
    }

    public boolean isNext() {
        return next;
    }

    public void setNext(boolean next) {
        this.next = next;
    }

    public CalculationResponse getResponse() {
        return response;
    }

    public void setResponse(CalculationResponse response) {
        this.response = response;
    }
}
