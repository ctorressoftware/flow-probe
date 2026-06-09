package io.github.ctorressoftware.model;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.stream.Stream;

public class Proxy {

    private final HttpClient client;

    public Proxy() {
        this.client = HttpClient.newHttpClient();
    }

    public ServiceResponse call(RequestFormat requestFormat) {
        HttpRequest request = resolveRequest(requestFormat);
        HttpResponse<String> response = null;

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println("Error: " + e.getMessage());
        }

        if (response == null) {
            return null;
        }

        return new ServiceResponse(response.statusCode(), response.body());
    }

    private HttpRequest resolveRequest(RequestFormat request) {

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
