package calculation.service.impl;

import calculation.service.CalculationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
class CalculationServiceImpl implements CalculationService {

    private static final Logger logger = LoggerFactory.getLogger(CalculationServiceImpl.class);

    public Integer multipleByTwo(Integer parameter) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            logger.error(e.toString());
        }
        return parameter * 2;
    }

    public Integer multipleByThree(Integer parameter) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            logger.error(e.toString());
        }
        return parameter * 3;
    }
}
