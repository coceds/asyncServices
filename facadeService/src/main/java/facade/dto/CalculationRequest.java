package facade.dto;

import java.math.BigDecimal;

public class CalculationRequest {

    public CalculationRequest() {
    }

    public CalculationRequest(BigDecimal parameter) {
        this.parameter = parameter;
    }

    private BigDecimal parameter;

    public BigDecimal getParameter() {
        return parameter;
    }

    public void setParameter(BigDecimal parameter) {
        this.parameter = parameter;
    }
}
