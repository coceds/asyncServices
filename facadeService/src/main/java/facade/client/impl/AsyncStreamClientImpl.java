package facade.client.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import facade.client.AsyncStreamClient;
import facade.client.CalculationEndPoints;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import rx.Observable;
import rx.apache.http.ObservableHttp;
import rx.apache.http.ObservableHttpResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Service
public class AsyncStreamClientImpl implements AsyncStreamClient {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private CloseableHttpAsyncClient asyncClient;
    @Value("#{properties.calculationServiceUrl}")
    private String baseUrl;

    public <T> Observable<T> createPost(CalculationEndPoints endPoint, Object request) {
        try {
            String content = "";
            if (request != null) {
                content = mapper.writeValueAsString(request);
            }
            final HttpAsyncRequestProducer requestProducer = HttpAsyncMethods.createPost(baseUrl + endPoint.getUrl(), content,
                    ContentType.APPLICATION_JSON);
            return processRequest(endPoint.getTypeReference(), requestProducer);
        } catch (UnsupportedEncodingException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> Observable<T> processRequest(TypeReference typeReference, HttpAsyncRequestProducer requestProducer) {
        final Observable<ObservableHttpResponse> observable =
                ObservableHttp.createRequest(requestProducer, asyncClient)
                        .toObservable();

        return observable.flatMap(response -> {
            return Observable.<T>create(subscriber -> {
                response.getContent().subscribe(bytes -> {
                    try {
                        String value = new String(bytes);
                        value = value.replace("data:", "");
                        subscriber.onNext(mapper.<T>readValue(value, typeReference));
                        subscriber.onCompleted();
                    } catch (IOException e) {
                        subscriber.onError(e);
                    }
                }, throwable -> {
                    subscriber.onError(throwable);
                }, () -> {
                    subscriber.onCompleted();
                });
            });
        });

//        return Observable.<T>create(subscriber ->
//                observable.subscribe(observableHttpResponse -> {
//                    observableHttpResponse.getContent().subscribe(bytes -> {
//                        try {
//                            String value = new String(bytes);
//                            value = value.replace("data:", "");
//                            subscriber.onNext(mapper.<T>readValue(value, typeReference));
//                        } catch (IOException e) {
//                            subscriber.onError(e);
//                        }
//                    }, throwable -> {
//                        subscriber.onError(throwable);
//                    });
//                }, throwable -> {
//                    subscriber.onError(throwable);
//                }, () -> {
//                    subscriber.onCompleted();
//                })
//        );
    }
}
