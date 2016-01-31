package facade.service.impl;

import facade.service.Calculation;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
class CalculationImpl implements Calculation {

    public BigDecimal calculate(List<BigDecimal> values) {
        return values.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
