package calculation.service.impl;

import calculation.service.CalculationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
class CalculationServiceImpl implements CalculationService {

    private static final Logger logger = LoggerFactory.getLogger(CalculationServiceImpl.class);

    @Override
    public BigDecimal multipleByTwo(BigDecimal parameter) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            logger.error(e.toString());
        }
        return parameter.multiply(new BigDecimal("2"));
    }

    @Override
    public BigDecimal multipleByThree(BigDecimal parameter) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            logger.error(e.toString());
        }
        return parameter.multiply(new BigDecimal("3"));
    }
}
