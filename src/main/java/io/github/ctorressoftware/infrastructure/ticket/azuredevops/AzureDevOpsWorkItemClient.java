package io.github.ctorressoftware.infrastructure.ticket.azuredevops;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.ctorressoftware.domain.constant.HttpStatusCode;

// TODO: This is a temporary logic to test Azure DevOps API. Refactor this later.
public class AzureDevOpsWorkItemClient {

    private final HttpClient client;

    public AzureDevOpsWorkItemClient() {
        this.client = HttpClient.newHttpClient();
    }

    public AzureDevOpsWorkItemResponse createWorkItem(
            AzureDevOpsCreateWorkItemRequest request,
            AzureDevOpsConfiguration configuration) {

        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        String endpoint = configuration.azureOrganization() + "/" + configuration.azureProject()
                + "/_apis/wit/workitems/$" + configuration.azureWorkItemType() + "?api-version=7.2-preview.3";

        try {
            URI uri = URI.create(AzureDevOpsConfiguration.AZURE_BASE_URL + endpoint);
            String jsonBody = mapper.writeValueAsString(request.operations());

            String base64Credentials = Base64
                    .getEncoder()
                    .encodeToString((":" + configuration.azurePat())
                            .getBytes(StandardCharsets.UTF_8));

            HttpRequest httpRequest = HttpRequest
                    .newBuilder(uri)
                    .POST(BodyPublishers.ofString(jsonBody))
                    .header("Content-Type", "application/json-patch+json")
                    .header("Authorization", "Basic " + base64Credentials)
                    .build();

            HttpResponse<String> response = client.send(httpRequest, BodyHandlers.ofString());

            if (response.statusCode() < HttpStatusCode.OK || response.statusCode() >= HttpStatusCode.MULTIPLE_CHOICES) {
                throw new RuntimeException(
                        "Error when trying to create an impediment ticket in Azure. " +
                                "Status: " + response.statusCode() + ". Body: " + response.body()
                );
            }

            return mapper.readValue(response.body(), AzureDevOpsWorkItemResponse.class);

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error calling Azure DevOps API", e); // TODO: create a custom one
        }
    }
}
