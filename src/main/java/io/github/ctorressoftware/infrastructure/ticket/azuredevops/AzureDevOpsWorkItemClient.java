package io.github.ctorressoftware.infrastructure.ticket.azuredevops;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;

// TODO: This is a temporary logic to test Azure DevOps API. Refactor this later.
public class AzureDevOpsWorkItemClient {

    private final HttpClient client;

    public AzureDevOpsWorkItemClient() {
        this.client = HttpClient.newHttpClient();
    }

    public void createWorkItem(AzureDevOpsCreateWorkItemRequest request) {

        String endpoint = "/" + AzureSettings.AZURE_ORGANIZATION 
            + "/" + AzureSettings.AZURE_PROJECT 
            + "/_apis/wit/workitems/impediment?api-version=7.2-preview.3";

        URI uri = URI.create(AzureSettings.BASE_URL + endpoint);

        HttpRequest httpRequest = HttpRequest.newBuilder(uri).build();

        try {
            client.send(httpRequest, BodyHandlers.ofString());   
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("error");
        }
    }
}
