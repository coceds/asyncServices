package facade.client;

import com.fasterxml.jackson.core.type.TypeReference;
import facade.dto.CalculationResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

public enum CalculationEndPoints {

    MULTIPLY_BY_TWO(CalculationClient.MULTIPLY_BY_TWO, HttpMethod.POST, new TypeReference<CalculationResponse>() {
    }, CalculationResponse.class, MediaType.APPLICATION_JSON),
    MULTIPLY_BY_THREE(CalculationClient.MULTIPLY_BY_THREE, HttpMethod.POST, new TypeReference<CalculationResponse>() {
    }, CalculationResponse.class, MediaType.APPLICATION_JSON),
    RANDOM_STREAM(CalculationClient.RANDOM_STREAM, HttpMethod.POST, new TypeReference<CalculationResponse>() {
    }, CalculationResponse.class, MediaType.APPLICATION_JSON),
    RANDOM_STREAM_BOOLEAN(CalculationClient.RANDOM_STREAM_BOOLEAN, HttpMethod.POST, new TypeReference<CalculationResponse>() {
    }, CalculationResponse.class, MediaType.APPLICATION_JSON);


    private final String url;
    private final HttpMethod httpMethod;
    private final TypeReference<?> typeReference;
    private MediaType mediaType;
    private Class resultClass;


    public String getUrl() {
        return url;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public TypeReference<?> getTypeReference() {
        return typeReference;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public Class getResultClass() {
        return resultClass;
    }

    CalculationEndPoints(String url, HttpMethod httpMethod, TypeReference<?> typeReference, Class resultClass, MediaType mediaType) {
        this.url = url;
        this.httpMethod = httpMethod;
        this.typeReference = typeReference;
        this.resultClass = resultClass;
        this.mediaType = mediaType;
    }
}
