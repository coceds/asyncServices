package facade.client.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import facade.client.CalculationClient;
import facade.client.CalculationEndPoints;
import facade.dto.CalculationRequest;
import facade.dto.CalculationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;

import java.math.BigDecimal;

@Service
public class CalculationClientImpl implements CalculationClient {

    @Autowired
    private AsyncRestTemplate restTemplate;
    @Value("#{properties.calculationServiceUrl}")
    private String baseUrl;

    @Override
    public ListenableFuture<ResponseEntity<CalculationResponse>> multipleByTwo(BigDecimal parameter) {
        CalculationEndPoints endPoint = CalculationEndPoints.MULTIPLY_BY_TWO;
        return process(parameter, endPoint);
    }

    @Override
    public ListenableFuture<ResponseEntity<CalculationResponse>> multipleByThree(BigDecimal parameter) {
        CalculationEndPoints endPoint = CalculationEndPoints.MULTIPLY_BY_THREE;
        return process(parameter, endPoint);
    }

    private ListenableFuture<ResponseEntity<CalculationResponse>> process(BigDecimal parameter, CalculationEndPoints endPoint) {
        CalculationRequest request = new CalculationRequest();
        request.setParameter(parameter);
        HttpEntity<String> httpEntity = createHttpEntity(endPoint.getMediaType(), request);
        ListenableFuture<ResponseEntity<CalculationResponse>> futureEntity = restTemplate
                .exchange(baseUrl + endPoint.getUrl(), endPoint.getHttpMethod(), httpEntity,
                        CalculationResponse.class);
        return futureEntity;
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
