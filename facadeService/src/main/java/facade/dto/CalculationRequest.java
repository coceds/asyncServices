package facade.dto;

import java.math.BigDecimal;

public class CalculationRequest {

    private BigDecimal parameter;

    public CalculationRequest() {
    }

    public CalculationRequest(BigDecimal parameter) {
        this.parameter = parameter;
    }


    public BigDecimal getParameter() {
        return parameter;
    }

    public void setParameter(BigDecimal parameter) {
        this.parameter = parameter;
    }
}
