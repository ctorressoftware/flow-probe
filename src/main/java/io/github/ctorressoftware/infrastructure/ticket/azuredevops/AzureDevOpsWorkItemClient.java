package io.github.ctorressoftware.infrastructure.ticket.azuredevops;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

import io.github.ctorressoftware.application.port.out.JsonProcessor;
import io.github.ctorressoftware.domain.constant.HttpStatusCode;
import io.github.ctorressoftware.domain.exception.HttpServiceCallException;

public class AzureDevOpsWorkItemClient {

    private final static String AZURE_BASE_URL = "https://dev.azure.com/";
    private final static String AZURE_API_VERSION = "7.1";
    private final JsonProcessor jsonProcessor;
    private final HttpClient client;
    private final Duration requestTimeout;

    public AzureDevOpsWorkItemClient(
            JsonProcessor jsonProcessor,
            HttpClient client,
            Duration requestTimeout
    ) {
        this.jsonProcessor = jsonProcessor;
        this.client = client;
        this.requestTimeout = requestTimeout;
    }

    public AzureDevOpsWorkItemResponse createWorkItem(
            AzureDevOpsCreateWorkItemRequest request,
            AzureDevOpsConfig config
    ) {
        // TODO: Previous API version: 7.2-preview.3. Remove this note after testing impediment creation with 7.1.
        String endpoint = "%s/%s/_apis/wit/workitems/$%s?api-version=%s"
                .formatted(
                        encodePathSegment(config.organization()),
                        encodePathSegment(config.project()),
                        encodePathSegment(config.workItemType()),
                        encodeQueryParam(AZURE_API_VERSION)
                );

        try {
            URI uri = URI.create(AZURE_BASE_URL + endpoint);
            String jsonBody = jsonProcessor.serialize(request.operations());

            String base64Credentials = Base64
                    .getEncoder()
                    .encodeToString((":" + config.pat())
                            .getBytes(StandardCharsets.UTF_8));

            HttpRequest httpRequest = HttpRequest
                    .newBuilder(uri)
                    .POST(BodyPublishers.ofString(jsonBody))
                    .header("Content-Type", "application/json-patch+json")
                    .header("Authorization", "Basic " + base64Credentials)
                    .timeout(requestTimeout)
                    .build();

            HttpResponse<String> response = client.send(httpRequest, BodyHandlers.ofString());

            if (response.statusCode() < HttpStatusCode.OK || response.statusCode() >= HttpStatusCode.MULTIPLE_CHOICES) {
                throw new RuntimeException( // TODO: Create custom exception
                        "Error when trying to create an impediment ticket in Azure. " +
                                "Status: " + response.statusCode() + ". Body: " + response.body()
                );
            }

            return jsonProcessor.deserialize(response.body(), AzureDevOpsWorkItemResponse.class);

        } catch (HttpTimeoutException e) {
            throw new HttpServiceCallException("Azure DevOps service call timed out", e);
        } catch (IOException e) {
            throw new RuntimeException("Error calling Azure DevOps API", e); // TODO: Create custom exception
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Azure DevOps Service call was interrupted: ", e); // TODO: Create custom exception
        }
    }

    private String encodePathSegment(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8)
                .replace("+", "%20");
    }

    private String encodeQueryParam(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
