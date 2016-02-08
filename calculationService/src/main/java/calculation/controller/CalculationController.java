package calculation.controller;

import calculation.dto.CalculationRequest;
import calculation.dto.CalculationResponse;
import calculation.dto.FailedResponse;
import calculation.service.AsyncCalculationService;
import calculation.service.ObservableService;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import rx.Observable;

import java.io.IOException;
import java.math.BigDecimal;

@RestController
public class CalculationController {

    private static final Logger logger = LoggerFactory.getLogger(CalculationController.class);

    @Autowired
    private AsyncCalculationService asyncCalculationService;
    @Autowired
    private ObservableService observableService;

    @RequestMapping(value = "/test", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public CalculationResponse test() {
        throw new RuntimeException("test dasd ");
    }

    @RequestMapping(value = "/randomStream", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public SseEmitter randomStream(@RequestBody CalculationRequest request) {
        logger.info("/randomStream call");
        Observable<BigDecimal> o = observableService.getRandomStream(new BigDecimal("10"));
        return setEmitter(o);
    }

    @RequestMapping(value = "/randomStreamBoolean", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public SseEmitter randomStreamBoolean() {
        logger.info("/randomStreamBoolean call");
        Observable<Boolean> o = observableService.getRandomStreamBoolean();
        return setEmitter(o);
    }

    private CalculationResponse createResponse(Object b) {
        if (b instanceof Boolean) {
            return new CalculationResponse((boolean) b);
        } else if (b instanceof BigDecimal) {
            return new CalculationResponse((BigDecimal) b);
        } else if (b instanceof String) {
            return new CalculationResponse((String) b);
        } else {
            throw new RuntimeException("unexpected type");
        }
    }

    private <T> SseEmitter setEmitter(Observable<T> observable) {
        final SseEmitter responseBodyEmitter = new SseEmitter();
        observable.subscribe(m -> {
            try {
                logger.info("send response");
                responseBodyEmitter.send(createResponse(m));
            } catch (IOException e) {
                logger.info("io exception");
                responseBodyEmitter.completeWithError(e);
            }
        }, e -> {
            logger.info("on fail");
            responseBodyEmitter.completeWithError(e);

        }, () -> {
            logger.info("complete");
            responseBodyEmitter.complete();
        });
        //
        return responseBodyEmitter;
    }

    @RequestMapping(value = "/multipleByTwo", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public DeferredResult<CalculationResponse> multipleByTwo(@RequestBody CalculationRequest request) {
        ListenableFuture<BigDecimal> response = asyncCalculationService.multipleByTwo(request.getParameter());
        return setResult(response);
    }

    @RequestMapping(value = "/multipleByThree", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public DeferredResult<CalculationResponse> multipleByThree(@RequestBody CalculationRequest request) {
        ListenableFuture<BigDecimal> response = asyncCalculationService.multipleByThree(request.getParameter());
        return setResult(response);
    }

    private DeferredResult<CalculationResponse> setResult(ListenableFuture<BigDecimal> response) {
        final DeferredResult<CalculationResponse> result = new DeferredResult<>();
        Futures.addCallback(response, new FutureCallback<BigDecimal>() {
            @Override
            public void onSuccess(BigDecimal res) {
                if (res == null) {
                    result.setErrorResult(new RuntimeException("Calculation failed."));
                } else {
                    result.setResult(new CalculationResponse(res));
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                result.setErrorResult(throwable);

            }
        });
        return result;
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Exception.class})
    public FailedResponse handleException(Exception e) {
        logger.error("Calculation failed.", e);
        return new FailedResponse(e, "Calculation failed.");
    }
}
