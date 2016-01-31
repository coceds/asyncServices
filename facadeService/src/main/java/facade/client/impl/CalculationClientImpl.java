package facade.client.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import facade.client.CalculationClient;
import facade.client.CalculationEndPoints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;

@Service
class CalculationClientImpl implements CalculationClient {

    @Autowired
    private AsyncRestTemplate restTemplate;
    @Value("#{properties.calculationServiceUrl}")
    private String baseUrl;

    @Override
    public <T, K> ListenableFuture<ResponseEntity<T>> multipleByTwo(K request) {
        return process(request, CalculationEndPoints.MULTIPLY_BY_TWO);
    }

    @Override
    public <T, K> ListenableFuture<ResponseEntity<T>> multipleByThree(K request) {
        return process(request, CalculationEndPoints.MULTIPLY_BY_THREE);
    }

    private <T, K> ListenableFuture<ResponseEntity<T>> process(K request, CalculationEndPoints endPoint) {
        HttpEntity<String> httpEntity = createHttpEntity(endPoint.getMediaType(), request);
        return restTemplate.exchange(baseUrl + endPoint.getUrl(), endPoint.getHttpMethod(), httpEntity,
                endPoint.getResultClass());
    }

    private HttpEntity<String> createHttpEntity(MediaType mediaType, Object entity) {
        try {
            HttpHeaders headers = setContentType(mediaType);
            String body = null;
            if (entity != null) {
                if (entity instanceof String) {
                    body = (String) entity;
                } else {
                    body = new ObjectMapper().writeValueAsString(entity);
                }
            }

            HttpEntity<String> httpEntity;
            if (body != null) {
                httpEntity = new HttpEntity<>(body, headers);
            } else {
                httpEntity = new HttpEntity<>(headers);
            }
            return httpEntity;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not call service", e);
        }
    }

    private HttpHeaders setContentType(MediaType mediaType) {
        HttpHeaders headers = new HttpHeaders();
        if (mediaType != null) {
            headers.setContentType(mediaType);
        }
        return headers;
    }
}
