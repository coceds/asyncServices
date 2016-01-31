package facade.service;

import java.math.BigDecimal;
import java.util.List;

public interface Calculation {

    BigDecimal calculate(List<BigDecimal> values);
}
