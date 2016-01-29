package facade.client;

import com.fasterxml.jackson.core.type.TypeReference;
import facade.dto.CalculationResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

public enum CalculationEndPoints {

    MULTIPLY_BY_TWO(CalculationClient.MULTIPLY_BY_TWO, HttpMethod.POST, new TypeReference<CalculationResponse>() {
    }, MediaType.APPLICATION_JSON),
    MULTIPLY_BY_THREE(CalculationClient.MULTIPLY_BY_THREE, HttpMethod.POST, new TypeReference<CalculationResponse>() {
    }, MediaType.APPLICATION_JSON);

    private final String url;
    private final HttpMethod httpMethod;
    private final TypeReference<?> typeReference;
    private MediaType mediaType;

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

    CalculationEndPoints(final String url, final HttpMethod httpMethod, final TypeReference<?> typeReference) {
        this.url = url;
        this.httpMethod = httpMethod;
        this.typeReference = typeReference;
    }

    CalculationEndPoints(final String url, final HttpMethod httpMethod, final TypeReference<?> typeReference,
                         final MediaType mediaType) {
        this(url, httpMethod, typeReference);
        this.mediaType = mediaType;
    }
}
