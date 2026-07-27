package io.github.ctorressoftware.infrastructure.callservice;

import io.github.ctorressoftware.application.port.out.ServiceCaller;
import io.github.ctorressoftware.domain.constant.HttpStatusCode;
import io.github.ctorressoftware.domain.exception.HttpServiceCallException;
import io.github.ctorressoftware.domain.model.ServiceCall;
import io.github.ctorressoftware.domain.model.CallResult;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

public class RestServiceCaller implements ServiceCaller {

    private final HttpClient client;
    private final RequestMapper mapper;

    public RestServiceCaller(HttpClient client, RequestMapper mapper) {
        this.client = Objects.requireNonNull(client);
        this.mapper = Objects.requireNonNull(mapper);
    }

    public CallResult call(ServiceCall serviceCall) {
        HttpRequest request = mapper.map(serviceCall);
        HttpResponse<String> response = null;

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new HttpServiceCallException("Failed to call service: " + serviceCall.url(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new HttpServiceCallException("Service call was interrupted: " + serviceCall.url(), e);
        }

        if (response == null) {
            return new CallResult(HttpStatusCode.INTERNAL_SERVER_ERROR, null);
        }

        return new CallResult(response.statusCode(), response.body());
    }
}
