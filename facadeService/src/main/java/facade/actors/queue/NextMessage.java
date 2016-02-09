package facade.actors.queue;


import facade.dto.CalculationResponse;

public class NextMessage implements Message {

    private final CalculationResponse response;

    public NextMessage(CalculationResponse response) {
        this.response = response;
    }

    public CalculationResponse getResponse() {
        return response;
    }
}
