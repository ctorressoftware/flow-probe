package io.github.ctorressoftware.infrastructure.ticket.azuredevops;

import io.github.ctorressoftware.domain.model.ImpedimentTicket;

import java.util.List;

public record AzureDevOpsCreateWorkItemRequest(List<AzureDevOpsPatchOperation> operations) {

    public static AzureDevOpsCreateWorkItemRequest from(ImpedimentTicket ticket) {
        return new AzureDevOpsCreateWorkItemRequest(
                List.of(
                        AzureDevOpsPatchOperation.add(
                                "/fields/System.Title",
                                ticket.getTitle()
                        ),
                        AzureDevOpsPatchOperation.add(
                                "/fields/System.Description",
                                ticket.getDescription()
                        ),
                        AzureDevOpsPatchOperation.add(
                                "/multilineFieldsFormat/System.Description",
                                "Markdown"
                        )
                )
        );
    }
}