package facade.service.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.*;
import facade.client.CalculationClient;
import facade.dto.CalculationRequest;
import facade.dto.CalculationResponse;
import facade.service.AsyncFacadeService;
import facade.service.Calculation;
import facade.utils.ListenableUtils;
import fj.Unit;
import fj.control.parallel.Actor;
import fj.control.parallel.Strategy;
import fj.function.Effect1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import rx.Observable;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
class AsyncFacadeServiceImpl implements AsyncFacadeService {

    private static final Logger logger = LoggerFactory.getLogger(AsyncFacadeServiceImpl.class);

    @Autowired
    private CalculationClient calculationClient;
    @Autowired
    private Calculation calculation;
    @Value("#{properties.calculationServiceUrl}")
    private String baseUrl;
    private Strategy<Unit> strategy;

    private ListeningExecutorService executorService;
    private final Map<Integer, Actor> observableMap = new ConcurrentHashMap<>();
    private final AtomicInteger keyId = new AtomicInteger(0);

    @Override
    public Integer randomStreamWithActors(final BigDecimal parameter) {
        final Integer key = keyId.incrementAndGet();
        final Observable<CalculationResponse> state = randomStream(parameter);

        final Actor actor = Actor.queueActor(strategy, new Effect1() {
            private boolean finish = false;
            private final Queue<CalculationResponse> elements = new LinkedList<>();
            private final Queue<SettableFuture<CalculationResponse>> futures = new LinkedList<>();

            @Override
            public void f(Object object) {
                if (object instanceof Boolean) {
                    finish = (Boolean) object;
                    if (finish && CollectionUtils.isEmpty(elements)) {
                        while (!CollectionUtils.isEmpty(futures)) {
                            SettableFuture<CalculationResponse> future = futures.poll();
                            future.setException(new RuntimeException("no more elements."));
                        }
                    }
                } else if (object instanceof CalculationResponse) {
                    if (CollectionUtils.isEmpty(futures)) {
                        elements.add((CalculationResponse) object);
                    } else {
                        futures.poll().set((CalculationResponse) object);
                    }

                } else if (object instanceof SettableFuture) {
                    if (finish && CollectionUtils.isEmpty(elements)) {
                        ((SettableFuture) object).setException(new RuntimeException("queue is closed"));
                    } else if (elements.size() > 0) {
                        ((SettableFuture) object).set(elements.poll());
                    } else {
                        futures.add((SettableFuture) object);
                    }
                } else if (object instanceof Exception) {
                    finish = true;
                } else {
                    logger.error("invalid type");
                    throw new RuntimeException("invalid type");
                }
            }
        });
        state.subscribe(calculationResponse -> {
            actor.act(calculationResponse);
        }, throwable -> {
            actor.act(throwable);
        }, () -> {
            actor.act(true);
        });
        observableMap.put(key, actor);
        return key;
    }

    @Override
    public ListenableFuture<CalculationResponse> getNextById(Integer id) {
        Actor actor = observableMap.get(id);
        if (actor == null) {
            throw new RuntimeException("observable was not found.");
        }
        final SettableFuture<CalculationResponse> settableFuture = SettableFuture.create();
        actor.act(settableFuture);
        return settableFuture;
    }

    @Override
    public Observable<CalculationResponse> randomStream(BigDecimal parameter) {
        Observable<CalculationResponse> one = calculationClient.randomStreamBigDecimal(new CalculationRequest(parameter));
        Observable<CalculationResponse> two = calculationClient.randomStreamBoolean();
        Observable<CalculationResponse> observable = Observable.zip(one, two, (calculationResponse, calculationResponse2) -> {
            calculationResponse2.setResult(calculationResponse.getResult());
            return calculationResponse2;
        }).filter(calculationResponse -> calculationResponse.isFlag());
        return observable;
    }

    @Override
    public ListenableFuture<BigDecimal> calculate(BigDecimal parameter) {

        List<ListenableFuture<ResponseEntity<CalculationResponse>>> futures = new ArrayList<>();

        futures.add(ListenableUtils.convertFuture(
                calculationClient.multipleByTwo(new CalculationRequest(parameter))));

        futures.add(ListenableUtils.convertFuture(
                calculationClient.multipleByThree(new CalculationRequest(parameter))));


        ListenableFuture<List<ResponseEntity<CalculationResponse>>> successfulQueries = Futures.allAsList(futures);

        AsyncFunction<List<ResponseEntity<CalculationResponse>>, BigDecimal> queryFunction =
                (List<ResponseEntity<CalculationResponse>> entities) -> {
                    final List<BigDecimal> results = entities.stream().map(e -> {
                        if (e.getBody().getResult() == null) {
                            logger.error("calculation failed.");
                            throw new RuntimeException("calculation failed.");
                        } else {
                            return e.getBody().getResult();
                        }
                    }).collect(Collectors.toList());
                    return executorService.submit(() ->
                            calculation.calculate(ImmutableList.copyOf(results))
                    );
                };

        return Futures.transform(successfulQueries, queryFunction);
    }

    @Autowired
    public void setExecutorService(ExecutorService service) {
        executorService = MoreExecutors.listeningDecorator(service);
        strategy = Strategy.executorStrategy(service);
    }
}
