package facade.dto;


import java.math.BigDecimal;

public class CalculationResponse {

    private BigDecimal result;
    private String status;
    private boolean flag;

    public CalculationResponse() {
    }

    public CalculationResponse(BigDecimal result) {
        this.result = result;
    }

    public CalculationResponse(String status) {
        this.status = status;
    }

    public CalculationResponse(boolean flag) {
        this.flag = flag;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
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
