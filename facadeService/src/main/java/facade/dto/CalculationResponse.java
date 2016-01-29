package facade.dto;


import java.math.BigDecimal;

public class CalculationResponse {

    private BigDecimal result;

    private String status;

    public CalculationResponse() {
    }

    public CalculationResponse(BigDecimal result) {
        this.result = result;
    }

    public CalculationResponse(String status) {
        this.status = status;
    }

    public BigDecimal getResult() {
        return result;
    }

    public void setResult(BigDecimal result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
