package io.github.ctorressoftware.infrastructure.ticket.azuredevops;

import io.github.ctorressoftware.domain.model.ImpedimentTicket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AzureDevOpsImpedimentTicketCreatorAdapterTest {

    @Mock
    private AzureDevOpsWorkItemTicketCreator azureDevOpsWorkItemTicketCreator;

    @Test
    void shouldCreateImpedimentTicketFromAzureDevOpsResponse() {

        ImpedimentTicket inputTicket =
                ImpedimentTicket.create("Title", "Description");

        AzureDevOpsWorkItemResponse response =
                Mockito.mock(AzureDevOpsWorkItemResponse.class);

        AzureDevOpsWorkItemResponse.Fields fields =
                Mockito.mock(AzureDevOpsWorkItemResponse.Fields.class);

        Mockito
                .when(azureDevOpsWorkItemTicketCreator.create(inputTicket))
                .thenReturn(response);

        Mockito.when(response.id()).thenReturn(123);
        Mockito.when(response.fields()).thenReturn(fields);
        Mockito.when(fields.title()).thenReturn("Title");
        Mockito.when(fields.description()).thenReturn("Description");

        AzureDevOpsImpedimentTicketCreatorAdapter adapter =
                new AzureDevOpsImpedimentTicketCreatorAdapter(
                        azureDevOpsWorkItemTicketCreator
                );

        ImpedimentTicket result = adapter.create(inputTicket);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(123L, result.id());
        Assertions.assertEquals("Title", result.title());
        Assertions.assertEquals("Description", result.description());

        Mockito.verify(azureDevOpsWorkItemTicketCreator)
                .create(inputTicket);

        Mockito.verifyNoMoreInteractions(
                azureDevOpsWorkItemTicketCreator
        );
    }
}
