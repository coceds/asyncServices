package calculation.service.impl;

import calculation.service.ObservableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
public class ObservableServiceImpl implements ObservableService {

    private static final Logger logger = LoggerFactory.getLogger(ObservableServiceImpl.class);

    @Autowired
    private ExecutorService executorService;

    public Observable<BigDecimal> getRandomStream(BigDecimal max) {
        PublishSubject<BigDecimal> publishSubject = PublishSubject.create();
        createObs(max).subscribe(publishSubject);
//        return Observable.<CalculationResponse>create(s -> {
//            logger.info("new random");
//            BigDecimal random = new BigDecimal(ThreadLocalRandom.current().nextInt(0, max.toBigInteger().intValue()));
//            s.onNext(new CalculationResponse(random));
//            s.onCompleted();
//        }).subscribeOn(Schedulers.from(executorService)).delay(1000l, TimeUnit.MILLISECONDS).repeat(10l);
        return publishSubject;
    }

    private Observable<BigDecimal> createObs(BigDecimal max) {
        return Observable.<BigDecimal>create(s -> {
            logger.info("new random");
            BigDecimal random = new BigDecimal(ThreadLocalRandom.current().nextInt(0, max.toBigInteger().intValue()));
            s.onNext(random);
            s.onCompleted();
        }).subscribeOn(Schedulers.from(executorService)).delay(700l, TimeUnit.MILLISECONDS).repeat(3l);
//        List<BigDecimal> range = IntStream.range(0, max.toBigInteger().intValue())
//                .boxed().map(integer -> new BigDecimal(integer))
//                .limit(10)
//                .collect(Collectors.toList());
//        return Observable.from(range).delay(1000l, TimeUnit.MILLISECONDS);
    }

    private Observable<Boolean> createObs() {
//        List<Boolean> range = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            range.add(ThreadLocalRandom.current().nextBoolean());
//        }
//        return Observable.from(range).delay(1000l, TimeUnit.MILLISECONDS);
        return Observable.<Boolean>create(s -> {
            logger.info("new random boolean");
            s.onNext(ThreadLocalRandom.current().nextBoolean());
            s.onCompleted();
        }).subscribeOn(Schedulers.from(executorService)).delay(800l, TimeUnit.MILLISECONDS).repeat(3l);
    }

    @Override
    public Observable<Boolean> getRandomStreamBoolean() {
        PublishSubject<Boolean> publishSubject = PublishSubject.create();
        createObs().subscribe(publishSubject);
//        return Observable.<CalculationResponse>create(s -> {
//            logger.info("new random boolean");
//            s.onNext(new CalculationResponse(ThreadLocalRandom.current().nextBoolean()));
//            s.onCompleted();
//        }).subscribeOn(Schedulers.from(executorService)).delay(1500l, TimeUnit.MILLISECONDS).repeat(10l);
        return publishSubject;
    }

}
