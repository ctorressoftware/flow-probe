package io.github.ctorressoftware.infrastructure.callservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ctorressoftware.domain.constant.HttpMethod;
import io.github.ctorressoftware.domain.exception.HttpServiceCallException;
import io.github.ctorressoftware.domain.model.ServiceCall;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class RequestMapper {

    private final ObjectMapper objectMapper;

    public RequestMapper(ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper);
    }

    public HttpRequest map(ServiceCall request) {

        HttpRequest.BodyPublisher body = request.body() == null || request.method().equals(HttpMethod.GET) ?
                HttpRequest.BodyPublishers.noBody() :
                HttpRequest.BodyPublishers.ofString(serializeBody(request.body()));

        Map<String, String> headers =
                request.headers() == null ? Map.of() : request.headers();

        String[] headersArray = headers.entrySet().stream()
                .flatMap(entry ->
                        Stream.of(entry.getKey(), String.valueOf(entry.getValue()))
                )
                .toArray(String[]::new);

        return HttpRequest.newBuilder()
                .uri(URI.create(request.url()))
                .headers(headersArray)
                .method(request.method(), body)
                .build();
    }

    private String serializeBody(Object body) {
        if (body instanceof String stringBody) {
            return stringBody;
        }
        try {
            return objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new HttpServiceCallException("Could not serialize request body to JSON", e);
        }
    }
}
