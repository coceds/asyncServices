package facade.dto;

import java.math.BigDecimal;

public class CalculationRequest {

    private BigDecimal parameter;

    public BigDecimal getParameter() {
        return parameter;
    }

    public void setParameter(BigDecimal parameter) {
        this.parameter = parameter;
    }
}
