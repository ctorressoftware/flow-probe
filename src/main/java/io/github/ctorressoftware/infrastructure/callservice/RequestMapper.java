package io.github.ctorressoftware.infrastructure.callservice;

import io.github.ctorressoftware.application.exception.JsonSerializationException;
import io.github.ctorressoftware.application.port.out.JsonProcessor;
import io.github.ctorressoftware.domain.exception.HttpServiceCallException;
import io.github.ctorressoftware.domain.model.ServiceCall;

import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.Objects;

public class RequestMapper {

    private final JsonProcessor jsonProcessor;
    private final Duration requestTimeout;

    public RequestMapper(JsonProcessor jsonProcessor, Duration requestTimeout) {
        this.jsonProcessor = Objects.requireNonNull(jsonProcessor);
        this.requestTimeout = Objects.requireNonNull(requestTimeout);
    }

    public HttpRequest map(ServiceCall request) {

        HttpRequest.BodyPublisher body = request.body() == null
                ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(serializeBody(request.body())
        );

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(request.url()))
                .method(request.method(), body)
                .timeout(requestTimeout);

        if (request.headers() != null) {
            request.headers().forEach(builder::header);
        }

        return builder.build();
    }

    private String serializeBody(Object body) {
        try {
            return jsonProcessor.serialize(body);
        } catch (JsonSerializationException e) {
            throw new HttpServiceCallException("Could not serialize request body to JSON", e);
        }
    }
}
