package io.github.ctorressoftware.infrastructure.ticket.azuredevops;

import io.github.ctorressoftware.domain.model.ImpedimentTicket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class AzureDevOpsCreateWorkItemRequestTest {

    @Test
    void shouldCreateValidAzureDevOpsRequestFromImpedimentTicket() {

        ImpedimentTicket ticket = ImpedimentTicket
                .create("Title", "Description");

        List<AzureDevOpsPatchOperation> operations = List.of(
                AzureDevOpsPatchOperation.add(
                        "/fields/System.Title",
                        "Title"
                ),
                AzureDevOpsPatchOperation.add(
                        "/fields/System.Description",
                        "Description"
                ),
                AzureDevOpsPatchOperation.add(
                        "/multilineFieldsFormat/System.Description",
                        "Markdown"
                )
        );

        AzureDevOpsCreateWorkItemRequest expected =
                new AzureDevOpsCreateWorkItemRequest(operations);

        Assertions.assertEquals(
                expected,
                AzureDevOpsCreateWorkItemRequest.from(ticket)
        );
    }
}
