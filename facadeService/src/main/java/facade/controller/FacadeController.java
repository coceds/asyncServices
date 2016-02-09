package facade.controller;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import facade.dto.*;
import facade.service.AsyncFacadeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import rx.Observable;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class FacadeController {

    private static final Logger logger = LoggerFactory.getLogger(FacadeController.class);

    @Autowired
    private AsyncFacadeService asyncFacadeService;

    @RequestMapping(value = "/calculateHateoas", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public DeferredResult<Simple> calculateHateoas(@RequestBody CalculationRequest request) {
        final ListenableFuture<BigDecimal> future =
                asyncFacadeService.calculate(request.getParameter());
        final DeferredResult<Simple> result = new DeferredResult<>();
        final List<Link> immutable = ImmutableList.of(linkTo(methodOn(FacadeController.class)
                .calculateHateoas(request)).withSelfRel());
        Futures.addCallback(future, new FutureCallback<BigDecimal>() {
            @Override
            public void onSuccess(BigDecimal response) {
                if (response == null) {
                    result.setErrorResult(new RuntimeException("Calculation failed."));
                } else {
                    Simple simple = new Simple(response);
                    simple.add(immutable);
                    result.setResult(simple);
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                result.setErrorResult(throwable);
            }
        });
        return result;
    }


    //@RequestBody CalculationRequest request
    @RequestMapping(value = "/randomStream", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public SseEmitter randomStream() {
        final SseEmitter emitter = new SseEmitter();
        Observable<CalculationResponse> o = asyncFacadeService.randomStream(new BigDecimal("10"));
        o.subscribe(m -> {
            try {
                emitter.send(m);
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        }, e -> {
            emitter.completeWithError(e);
        }, () -> {
            System.out.println("emitter complete");
            emitter.complete();
        });
        return emitter;
    }


    @RequestMapping(value = "/calculateWithActor", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public DeferredResult<CalculationResponseResource> calculateWithActor() {
        final Integer key = asyncFacadeService.randomStreamWithActors(new BigDecimal("10"));
        ListenableFuture<CalculationResponse> future = asyncFacadeService.getNextById(key);
        Link self = linkTo(methodOn(FacadeController.class).calculateWithActor()).withSelfRel();
        Link next = linkTo(methodOn(FacadeController.class).getByActorId(key)).withRel("next");
        return setResultForActor(future, ImmutableList.of(self, next));
    }

    @RequestMapping(value = "/getByActorId", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public DeferredResult<CalculationResponseResource> getByActorId(@RequestParam Integer id) {
        final ListenableFuture<CalculationResponse> future = asyncFacadeService.getNextById(id);
        Link next = linkTo(methodOn(FacadeController.class).getByActorId(id)).withRel("next");
        return setResultForActor(future, ImmutableList.of(next));
    }


    @RequestMapping(value = "/calculate", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public DeferredResult<CalculationResponse> calculate(@RequestBody CalculationRequest request) {
        final ListenableFuture<BigDecimal> future =
                asyncFacadeService.calculate(request.getParameter());
        return setResult(future);
    }

    private DeferredResult<CalculationResponse> setResult(ListenableFuture<BigDecimal> response) {
        final DeferredResult<CalculationResponse> result = new DeferredResult<>();
        Futures.addCallback(response, new FutureCallback<BigDecimal>() {
            @Override
            public void onSuccess(BigDecimal response) {
                if (response == null) {
                    result.setErrorResult(new RuntimeException("Calculation failed."));
                } else {
                    result.setResult(new CalculationResponse(response));
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                result.setErrorResult(throwable);
            }
        });
        return result;
    }

    private DeferredResult<CalculationResponseResource> setResultForActor(ListenableFuture<CalculationResponse> response, List<Link> links) {
        final DeferredResult<CalculationResponseResource> result = new DeferredResult<>();
        Futures.addCallback(response, new FutureCallback<CalculationResponse>() {
            @Override
            public void onSuccess(CalculationResponse response) {
                CalculationResponseResource res = new CalculationResponseResource(response);
                res.add(links);
                result.setResult(res);
            }

            @Override
            public void onFailure(Throwable throwable) {
                result.setErrorResult(throwable);
            }
        });
        return result;

    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public FailedResponse handleException(Exception e) {
        logger.error("Calculation failed.", e);
        return new FailedResponse(e, "Calculation failed.");
    }
}
