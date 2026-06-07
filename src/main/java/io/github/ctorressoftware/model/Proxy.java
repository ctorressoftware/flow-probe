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

    public void call(RequestFormat requestFormat) {
        HttpRequest request = resolveRequest(requestFormat);

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Status Code: " + response.statusCode());
            System.out.println("Response Body:\n" + response.body());

        } catch (IOException | InterruptedException e) {
            System.out.println("Error: " + e.getMessage());
        }

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
