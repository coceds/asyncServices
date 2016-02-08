package facade.client.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
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

            ObservableHttp<ObservableHttpResponse> req = ObservableHttp.createRequest(requestProducer, asyncClient);


            final Observable<ObservableHttpResponse> observable = req
                    .toObservable().doOnCompleted(() -> {
                        System.out.println("111111111");
                    });
            return Observable.create(subscriber -> {
                observable.subscribe(observableHttpResponse -> {
                    System.out.println("3");
                    observableHttpResponse.getContent().subscribe(bytes -> {
                        System.out.println("4");
                        try {
                            String value = new String(bytes);
                            value = value.replace("data:", "");
                            System.out.println(value);
                            subscriber.onNext((T) mapper.readValue(value, endPoint.getTypeReference()));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }, throwable -> {
                    subscriber.onError(throwable);
                }, () -> {
                    subscriber.onCompleted();
                });
            });
//            Observable.create(subscriber -> {
//                observable.map(observableHttpResponse -> {
//                    observableHttpResponse.getContent().subscribe(subscriber);
//                    return null;
//                });
//            });
//            Func1<ObservableHttpResponse, Observable<? extends T>> func = observableHttpResponse -> {
//                Observable<T> o = Observable.create(subscriber -> {
//                    observableHttpResponse.getContent().map(bb ->
//                    {
//                        try {
//                            String value = new String(bb);
//                            value = value.replace("data:", "");
//                            System.out.println(value);
//                            subscriber.onNext((T) mapper.readValue(value, endPoint.getTypeReference()));
//                            subscriber.onCompleted();
//                            return mapper.readValue(value, endPoint.getTypeReference());
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
//                    });
//                });
//                return o;
//                Observable<byte[]> content1 = observableHttpResponse.getContent();
//                content1.doOnCompleted(() -> {
//                    System.out.println("22222222");
//                });
//                return content1.map(bb ->
//                        {
//                            try {
//                                String value = new String(bb);
//                                value = value.replace("data:", "");
//                                System.out.println(value);
//                                return mapper.readValue(value, endPoint.getTypeReference());
//                            } catch (IOException e) {
//                                throw new RuntimeException(e);
//                            }
//                        }
//                );
//            };
//            final Observable<T> tObservable = observable.flatMap(func);
//            return tObservable;
//            return Observable.zip(observable, tObservable, (observableHttpResponse, t) -> {
//                return t;
//            });

        } catch (UnsupportedEncodingException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
