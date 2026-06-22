package io.github.ctorressoftware.infrastructure.ticket.azuredevops;

public class AzureDevOpsWorkItemTicketCreator {

    private final AzureDevOpsWorkItemClient azureDevOpsWorkItemClient;

    public AzureDevOpsWorkItemTicketCreator(AzureDevOpsWorkItemClient azureDevOpsWorkItemClient) {
        this.azureDevOpsWorkItemClient = azureDevOpsWorkItemClient;
    }

    public void create() {}
}
