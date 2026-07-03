package io.github.ctorressoftware.infrastructure.callservice;

import io.github.ctorressoftware.application.port.out.ServiceCaller;
import io.github.ctorressoftware.domain.constant.HttpMethod;
import io.github.ctorressoftware.domain.exception.HttpServiceCallException;
import io.github.ctorressoftware.domain.model.ServiceCall;
import io.github.ctorressoftware.domain.model.CallResult;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.stream.Stream;

public class RestServiceCaller implements ServiceCaller {

    private final HttpClient client;

    public RestServiceCaller() {
        this.client = HttpClient.newHttpClient();
    }

    public CallResult call(ServiceCall serviceCall) {
        HttpRequest request = resolveRequest(serviceCall);
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
            return null;
        }

        return new CallResult(response.statusCode(), response.body());
    }

    private HttpRequest resolveRequest(ServiceCall request) {

        HttpRequest.BodyPublisher body = request.body() == null || request.method().equals(HttpMethod.GET) ?
                HttpRequest.BodyPublishers.noBody() :
                HttpRequest.BodyPublishers.ofString(request.body().toString());

        String[] headers = request.headers()
                .entrySet()
                .stream()
                .flatMap(entry -> Stream.of(entry.getKey(), String.valueOf(entry.getValue())))
                .toArray(String[]::new);

        return HttpRequest.newBuilder()
                .uri(URI.create(request.url()))
                .headers(headers)
                .method(request.method(), body)
                .build();
    }

}
