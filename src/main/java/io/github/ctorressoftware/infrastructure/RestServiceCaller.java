package io.github.ctorressoftware.infrastructure;

import io.github.ctorressoftware.application.port.out.ServiceCaller;
import io.github.ctorressoftware.domain.constant.HttpMethod;
import io.github.ctorressoftware.domain.model.ServiceCall;
import io.github.ctorressoftware.domain.model.CallResult;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.stream.Stream;

public class Proxy implements ServiceCaller {

    private final HttpClient client;

    public Proxy() {
        this.client = HttpClient.newHttpClient();
    }

    public CallResult call(ServiceCall serviceCall) {
        HttpRequest request = resolveRequest(serviceCall);
        HttpResponse<String> response = null;

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println("Error: " + e.getMessage());
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
