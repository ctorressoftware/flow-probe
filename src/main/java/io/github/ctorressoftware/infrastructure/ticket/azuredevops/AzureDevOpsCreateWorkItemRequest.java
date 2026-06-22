package io.github.ctorressoftware.infrastructure.ticket.azuredevops;

import io.github.ctorressoftware.domain.model.ImpedimentTicket;

import java.util.List;

public record AzureDevOpsCreateWorkItemRequest(
        String workItemType,
        List<AzureDevOpsPatchOperation> operations
) {

    public static AzureDevOpsCreateWorkItemRequest from(ImpedimentTicket ticket) {
        return new AzureDevOpsCreateWorkItemRequest(
                "Product Backlog Item",
                List.of(
                        AzureDevOpsPatchOperation.add(
                                "/fields/System.Title",
                                ticket.getDescription()
                        ),
                        AzureDevOpsPatchOperation.add(
                                "/fields/System.Description",
                                ticket.getDescription()
                        )
                )
        );
    }
}