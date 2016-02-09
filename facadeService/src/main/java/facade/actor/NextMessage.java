package facade.actor;


import facade.dto.CalculationResponse;

public class NextMessage implements Message {

    private CalculationResponse response;

    public NextMessage(CalculationResponse response) {
        this.response = response;
    }

    public CalculationResponse getResponse() {
        return response;
    }

    public void setResponse(CalculationResponse response) {
        this.response = response;
    }
}
