package io.github.ctorressoftware.infrastructure.ticket.azuredevops;

import java.net.http.HttpClient;

public class AzureDevOpsWorkItemClient {

    private final HttpClient client;

    public AzureDevOpsWorkItemClient() {
        this.client = HttpClient.newHttpClient();
    }

    public void createWorkItem(AzureDevOpsCreateWorkItemRequest request) {

    }
}
