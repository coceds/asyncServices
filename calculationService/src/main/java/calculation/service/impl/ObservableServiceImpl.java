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
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
public class ObservableServiceImpl implements ObservableService {

    private static final Logger logger = LoggerFactory.getLogger(ObservableServiceImpl.class);

    @Autowired
    private ExecutorService executorService;

    public Observable<CalculationResponse> getRandomStream(BigDecimal max) {
        return Observable.<CalculationResponse>create(s -> {
            logger.info("new random");
            BigDecimal random = new BigDecimal(ThreadLocalRandom.current().nextInt(0, max.toBigInteger().intValue()));
            s.onNext(new CalculationResponse(random));
            s.onCompleted();
        }).subscribeOn(Schedulers.from(executorService)).delay(1000l, TimeUnit.MILLISECONDS).repeat(10l);
    }

    @Override
    public Observable<CalculationResponse> getRandomStreamBoolean() {
        return Observable.<CalculationResponse>create(s -> {
            logger.info("new random boolean");
            s.onNext(new CalculationResponse(ThreadLocalRandom.current().nextBoolean()));
            s.onCompleted();
        }).subscribeOn(Schedulers.from(executorService)).delay(1500l, TimeUnit.MILLISECONDS).repeat(10l);
    }

}