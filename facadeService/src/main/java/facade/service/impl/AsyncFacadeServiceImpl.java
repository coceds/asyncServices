package facade.service.impl;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.*;
import facade.actors.manager.Action;
import facade.actors.manager.CreateAction;
import facade.actors.manager.DeleteAction;
import facade.actors.manager.FindAction;
import facade.actors.queue.*;
import facade.client.CalculationClient;
import facade.dto.CalculationRequest;
import facade.dto.CalculationResponse;
import facade.dto.CreateActorRequest;
import facade.dto.FutureResponse;
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
import rx.Observable;

import java.math.BigDecimal;
import java.util.*;
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

    private final Actor<Action> manager = Actor.queueActor(strategy, new Effect1<Action>() {

        private final Map<String, Actor<Message>> actors = new HashMap<>();

        @Override
        public void f(Action action) {
            if (action instanceof CreateAction) {
                CreateAction ac = (CreateAction) action;
                String uuid = UUID.randomUUID().toString();
                final Actor<Message> actor = Actor.queueActor(strategy, Effect.getInstance());
                actors.put(uuid, actor);
                CreateActorRequest<Message> response = new CreateActorRequest<>(uuid, actor);
                ac.getFuture().set(response);
            } else if (action instanceof FindAction) {
                FindAction ac = (FindAction) action;
                Actor<Message> actor = actors.get(ac.getUuid());
                if (actor != null) {
                    ac.getFuture().set(actor);
                } else {
                    ac.getFuture().setException(new RuntimeException("actor not found."));
                }
            } else if (action instanceof DeleteAction) {
                DeleteAction ac = (DeleteAction) action;
                actors.remove(ac.getUuid());
            }
        }
    });

    public ListenableFuture<String> randomStreamWithManagerActors(final BigDecimal parameter) {
        final Observable<CalculationResponse> state = randomStream(parameter);
        final SettableFuture<CreateActorRequest<Message>> future = SettableFuture.create();
        final SettableFuture<String> result = SettableFuture.create();
        manager.act(new CreateAction(future));
        Futures.addCallback(future, new FutureCallback<CreateActorRequest<Message>>() {
            @Override
            public void onSuccess(CreateActorRequest<Message> request) {
                state.subscribe(calculationResponse ->
                                request.getActor().act(new NextMessage(calculationResponse))
                        , throwable ->
                                request.getActor().act(new ExceptionMessage(throwable))
                        , () ->
                                request.getActor().act(new FinishMessage())
                );
                result.set(request.getUuid());
            }

            @Override
            public void onFailure(Throwable throwable) {
                result.setException(throwable);
            }
        });
        return result;
    }

    public ListenableFuture<FutureResponse<CalculationResponse>> getNextByIdWithManager(String uuid) {
        final SettableFuture<Actor<Message>> future = SettableFuture.create();
        manager.act(new FindAction(uuid, future));
        ListenableFuture<FutureResponse<CalculationResponse>> result = Futures.transform(future, new Function<Actor<Message>, FutureResponse<CalculationResponse>>() {
            @Override
            public FutureResponse<CalculationResponse> apply(Actor<Message> actor) {
                FutureResponse<CalculationResponse> futureResponse = new FutureResponse<>(SettableFuture.create());
                actor.act(new ReadMessage(futureResponse));
                return futureResponse;
            }
        });
        return result;
    }

    @Override
    public String randomStreamWithActors(final BigDecimal parameter) {
        String uuid = UUID.randomUUID().toString();

        final Observable<CalculationResponse> state = randomStream(parameter);

        final Actor<Message> actor = Actor.queueActor(strategy, Effect.getInstance());
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
    public FutureResponse<CalculationResponse> getNextById(String uuid) {
        Actor<Message> actor = observableMap.get(uuid);
        if (actor == null) {
            throw new RuntimeException("observable was not found.");
        }
        FutureResponse<CalculationResponse> futureResponse = new FutureResponse<>(SettableFuture.create());
        actor.act(new ReadMessage(futureResponse));
        return futureResponse;
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
    }
}
