package facade.service.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.*;
import facade.actors.manager.Action;
import facade.actors.manager.CreateAction;
import facade.actors.manager.FindAction;
import facade.actors.queue.*;
import facade.client.CalculationClient;
import facade.dto.CalculationRequest;
import facade.dto.CalculationResponse;
import facade.dto.ReadResponse;
import facade.service.AsyncFacadeService;
import facade.service.Calculation;
import facade.utils.ListenableUtils;
import fj.Unit;
import fj.control.parallel.Actor;
import fj.control.parallel.Strategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import rx.Observable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
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
    private final Map<String, Actor<Message>> observableMap = new ConcurrentHashMap<>();

    private Actor<Action> manager;

    @Override
    public String randomStreamWithManagerActors(final BigDecimal parameter) {
        String uuid = UUID.randomUUID().toString();
        final Actor<Message> actor = Actor.queueActor(strategy, Effect.getInstance(manager, uuid));
        final Observable<CalculationResponse> state = randomStream(parameter);
        state.subscribe(calculationResponse ->
                        actor.act(new NextMessage(calculationResponse))
                , throwable ->
                        actor.act(new ExceptionMessage(throwable))
                , () ->
                        actor.act(new FinishMessage())
        );
        manager.act(new CreateAction(actor, uuid));
        return uuid;
    }

    @Override
    public ListenableFuture<ReadResponse> getNextByIdWithManager(String uuid) {
        final SettableFuture<Actor<Message>> future = SettableFuture.create();
        manager.act(new FindAction(uuid, future));
        AsyncFunction<Actor<Message>, ReadResponse> function = new AsyncFunction<Actor<Message>, ReadResponse>() {
            @Override
            public ListenableFuture<ReadResponse> apply(Actor<Message> messageActor) throws Exception {
                if (messageActor == null) {
                    final SettableFuture<ReadResponse> future = SettableFuture.create();
                    future.setException(new RuntimeException("actor doest exist"));
                    return future;
                }
                final SettableFuture<ReadResponse> future = SettableFuture.create();
                messageActor.act(new ReadMessage(future));
                return future;
            }
        };
        ListenableFuture<ReadResponse> result = Futures.transform(future, function);
        return result;
    }

    @Override
    public String randomStreamWithActors(final BigDecimal parameter) {
        String uuid = UUID.randomUUID().toString();

        final Observable<CalculationResponse> state = randomStream(parameter);

        final Actor<Message> actor = Actor.queueActor(strategy, Effect.getInstance(manager, uuid));
        state.subscribe(calculationResponse ->
                        actor.act(new NextMessage(calculationResponse))
                , throwable ->
                        actor.act(new ExceptionMessage(throwable))
                , () ->
                        actor.act(new FinishMessage())
        );
        observableMap.put(uuid, actor);
        return uuid;
    }

    @Override
    public ListenableFuture<ReadResponse> getNextById(String uuid) {
        Actor<Message> actor = observableMap.get(uuid);
        if (actor == null) {
            throw new RuntimeException("observable was not found.");
        }
        final SettableFuture<ReadResponse> future = SettableFuture.create();
        actor.act(new ReadMessage(future));
        return future;
    }

    @Override
    public Observable<CalculationResponse> randomStream(BigDecimal parameter) {
        Observable<CalculationResponse> one = calculationClient.randomStreamBigDecimal(new CalculationRequest(parameter));
        Observable<CalculationResponse> two = calculationClient.randomStreamBoolean();
        return Observable.zip(one, two, (calculationResponse, calculationResponse2) -> {
            calculationResponse2.setResult(calculationResponse.getResult());
            return calculationResponse2;
        }).filter(CalculationResponse::isFlag);
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
        manager = Actor.queueActor(strategy, ManagerEffect.getInstance());
    }
}
