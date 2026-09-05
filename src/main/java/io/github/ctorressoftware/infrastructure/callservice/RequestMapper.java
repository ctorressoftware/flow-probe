package io.github.ctorressoftware.infrastructure.callservice;

import io.github.ctorressoftware.application.exception.JsonSerializationException;
import io.github.ctorressoftware.application.port.out.JsonProcessor;
import io.github.ctorressoftware.domain.constant.HttpMethod;
import io.github.ctorressoftware.domain.exception.HttpServiceCallException;
import io.github.ctorressoftware.domain.model.ServiceCall;

import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class RequestMapper {

    private final JsonProcessor jsonProcessor;
    private final Duration requestTimeout;

    public RequestMapper(JsonProcessor jsonProcessor, Duration requestTimeout) {
        this.jsonProcessor = Objects.requireNonNull(jsonProcessor);
        this.requestTimeout = Objects.requireNonNull(requestTimeout);
    }

    public HttpRequest map(ServiceCall request) {

        HttpRequest.BodyPublisher body = request.body() == null ?
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
                .timeout(requestTimeout)
                .build();
    }

    private String serializeBody(Object body) {
        try {
            return jsonProcessor.serialize(body);
        } catch (JsonSerializationException e) {
            throw new HttpServiceCallException("Could not serialize request body to JSON", e);
        }
    }
}
