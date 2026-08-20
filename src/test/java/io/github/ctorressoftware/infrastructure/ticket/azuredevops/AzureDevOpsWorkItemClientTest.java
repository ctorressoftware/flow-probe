package io.github.ctorressoftware.infrastructure.ticket.azuredevops;

import io.github.ctorressoftware.domain.constant.HttpStatusCode;
import io.github.ctorressoftware.domain.model.ImpedimentTicket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@ExtendWith(MockitoExtension.class)
public class AzureDevOpsWorkItemClientTest {

    @Mock
    private HttpClient httpClient;

    private AzureDevOpsWorkItemClient azureDevOpsClient;

    @BeforeEach
    void init() {
        this.azureDevOpsClient = new AzureDevOpsWorkItemClient(httpClient);
    }

    @Test
    @SuppressWarnings("unchecked") // TODO: add ArgumentCaptor
    void shouldCreateAzureDevOpsWorkItem() throws IOException, InterruptedException {

        ImpedimentTicket ticket = ImpedimentTicket.create("Title", "Description");

        AzureDevOpsCreateWorkItemRequest request = AzureDevOpsCreateWorkItemRequest.from(ticket);

        AzureDevOpsConfiguration configuration = new AzureDevOpsConfiguration(
                "Impediment",
                "Organization",
                "Project",
                "1234567890987654321"
        );

        HttpResponse<String> mockResponse = Mockito.mock();

        String mockJson = "{\"id\": 123, \"fields\": {\"System.Title\": \"Title\"}}";
        Mockito.when(mockResponse.statusCode()).thenReturn(HttpStatusCode.OK);
        Mockito.when(mockResponse.body()).thenReturn(mockJson);

        Mockito.doReturn(mockResponse)
                .when(httpClient)
                .send(Mockito.any(), Mockito.any(HttpResponse.BodyHandler.class));

        AzureDevOpsWorkItemResponse response = azureDevOpsClient
                .createWorkItem(request, configuration);

        Assertions.assertNotNull(response, "La respuesta no debería ser nula");
        Assertions.assertEquals(123, response.id());
        Mockito.verify(httpClient, Mockito.times(1))
                .send(Mockito.any(HttpRequest.class), Mockito.any(HttpResponse.BodyHandler.class));

    }
}
