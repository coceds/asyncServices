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
            return ObservableHttp.createRequest(requestProducer, asyncClient)
                    .toObservable()
                    .doOnCompleted(() -> {
                        System.out.println("completed");
                        throw new RuntimeException("close connection");
                    }).flatMap(observableHttpResponse -> {
                                return observableHttpResponse.getContent().map(bb ->
                                        {
                                            try {
                                                String value = new String(bb);
                                                value = value.replace("data:", "");
                                                System.out.println(value);
                                                return mapper.readValue(value, endPoint.getTypeReference());
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                );
                            }

                    );
        } catch (UnsupportedEncodingException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
