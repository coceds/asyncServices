package facade.actor;


import facade.dto.CalculationResponse;
import facade.dto.FutureResponse;

public class ReadMessage implements Message {

    private FutureResponse<CalculationResponse> future;

    public ReadMessage(FutureResponse<CalculationResponse> future) {
        this.future = future;
    }

    public FutureResponse<CalculationResponse> getFuture() {
        return future;
    }

    public void setFuture(FutureResponse<CalculationResponse> future) {
        this.future = future;
    }
}
