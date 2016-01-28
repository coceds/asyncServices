package calculation.dto;


public class CalculationResponse {

    private Integer result;

    private String status;

    public CalculationResponse(Integer result) {
        this.result = result;
    }

    public CalculationResponse(String status) {
        this.status = status;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
