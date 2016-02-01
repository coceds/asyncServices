package calculation.service.impl;

import calculation.dto.CalculationResponse;
import calculation.service.ObservableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;

@Service
public class ObservableServiceImpl implements ObservableService {

    private static final Logger logger = LoggerFactory.getLogger(ObservableServiceImpl.class);

    @Autowired
    private ExecutorService executorService;

    public Observable<CalculationResponse> getRandomStream() {
        return Observable.<CalculationResponse>create(s -> {
            logger.info("Start: Executing slow task in Service 1");
            delay(1000);
            s.onNext(new CalculationResponse(BigDecimal.ONE));
            logger.info("End: Executing slow task in Service 1");
            s.onCompleted();
        }).subscribeOn(Schedulers.from(executorService));
    }

    public static void delay(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
