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
import java.util.Objects;

import io.github.ctorressoftware.application.port.out.JsonProcessor;
import io.github.ctorressoftware.domain.constant.HttpStatusCode;
import io.github.ctorressoftware.infrastructure.ticket.azuredevops.exception.AzureDevOpsApiException;
import io.github.ctorressoftware.infrastructure.ticket.azuredevops.exception.AzureDevOpsException;

public class AzureDevOpsWorkItemClient {

    private static final String AZURE_BASE_URL = "https://dev.azure.com/";
    private static final String AZURE_API_VERSION = "7.1";
    private final JsonProcessor jsonProcessor;
    private final HttpClient client;
    private final Duration requestTimeout;

    public AzureDevOpsWorkItemClient(
            JsonProcessor jsonProcessor,
            HttpClient client,
            Duration requestTimeout
    ) {
        this.jsonProcessor = Objects.requireNonNull(jsonProcessor);
        this.client = Objects.requireNonNull(client);
        this.requestTimeout = Objects.requireNonNull(requestTimeout);
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
                        AZURE_API_VERSION
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
                throw new AzureDevOpsApiException(
                        "Azure DevOps returned an unsuccessful response while creating the ticket. " +
                                "Status: " + response.statusCode() + ". " +
                                "Body: " + response.body()
                );
            }

            return jsonProcessor.deserialize(response.body(), AzureDevOpsWorkItemResponse.class);

        } catch (HttpTimeoutException e) {
            throw new AzureDevOpsException("Azure DevOps service call timed out", e);
        } catch (IOException e) {
            throw new AzureDevOpsException("Error calling Azure DevOps API", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AzureDevOpsException("Azure DevOps service call was interrupted", e);
        }
    }

    private String encodePathSegment(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8)
                .replace("+", "%20");
    }
}
