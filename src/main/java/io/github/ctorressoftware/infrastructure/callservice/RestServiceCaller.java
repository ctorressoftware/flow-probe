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
import java.net.http.HttpTimeoutException;
import java.util.Objects;

public class RestServiceCaller implements ServiceCaller {

    private final HttpClient client;
    private final RequestMapper requestMapper;

    public RestServiceCaller(HttpClient client, RequestMapper requestMapper) {
        this.client = Objects.requireNonNull(client);
        this.requestMapper = Objects.requireNonNull(requestMapper);
    }

    public CallResult call(ServiceCall serviceCall) {
        HttpRequest request = requestMapper.map(serviceCall);
        HttpResponse<String> response = null;

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (HttpTimeoutException e) {
            throw new HttpServiceCallException("Service call timed out: " + serviceCall.url(), e);
        } catch (IOException e) {
            throw new HttpServiceCallException("Failed to call service: " + serviceCall.url(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new HttpServiceCallException("Service call was interrupted: " + serviceCall.url(), e);
        }

        /*
            TODO: Reconsider handling a null HttpClient.send() response as HTTP 500.
            HttpClient.send() should return an HttpResponse, so null would represent
            an unexpected internal condition rather than an actual server response.
            Consider throwing HttpServiceCallException instead of returning
            INTERNAL_SERVER_ERROR to avoid implying that the remote service responded with 500.
        */

        if (response == null) {
            return new CallResult(HttpStatusCode.INTERNAL_SERVER_ERROR, null);
        }

        return new CallResult(response.statusCode(), response.body());
    }
}
