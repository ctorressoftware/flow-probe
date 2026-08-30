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
class AzureDevOpsWorkItemClientTest {

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

        String mockJson = """
                {
                  "id": 123,
                  "rev": 4,
                  "fields": {
                    "System.AreaPath": "Project",
                    "System.TeamProject": "Project",
                    "System.IterationPath": "Project",
                    "System.WorkItemType": "Impediment",
                    "System.State": "New",
                    "System.Reason": "New",
                    "System.CreatedDate": "2026-08-30T12:00:00Z",
                    "System.CreatedBy": {
                      "displayName": "Carlos Torres",
                      "url": "https://example.com/identities/1",
                      "_links": {
                        "avatar": {
                          "href": "https://example.com/avatar/1"
                        }
                      },
                      "id": "identity-1",
                      "uniqueName": "carlos@example.com",
                      "imageUrl": "https://example.com/images/1",
                      "descriptor": "aad.identity-1"
                    },
                    "System.ChangedDate": "2026-08-30T13:00:00Z",
                    "System.ChangedBy": {
                      "displayName": "Another User",
                      "url": "https://example.com/identities/2",
                      "_links": {
                        "avatar": {
                          "href": "https://example.com/avatar/2"
                        }
                      },
                      "id": "identity-2",
                      "uniqueName": "another@example.com",
                      "imageUrl": "https://example.com/images/2",
                      "descriptor": "aad.identity-2"
                    },
                    "System.CommentCount": 0,
                    "System.Title": "Title",
                    "System.BoardColumn": "New",
                    "System.BoardColumnDone": false,
                    "Microsoft.VSTS.Common.StateChangeDate": "2026-08-30T12:30:00Z",
                    "Microsoft.VSTS.Common.Priority": 2,
                    "System.Description": "Description"
                  },
                  "_links": {
                    "self": {
                      "href": "https://example.com/workitems/123"
                    }
                  },
                  "url": "https://example.com/workitems/123"
                }
                """;

        Mockito.when(mockResponse.statusCode()).thenReturn(HttpStatusCode.OK);
        Mockito.when(mockResponse.body()).thenReturn(mockJson);

        Mockito.doReturn(mockResponse)
                .when(httpClient)
                .send(Mockito.any(HttpRequest.class), Mockito.any(HttpResponse.BodyHandler.class));

        AzureDevOpsWorkItemResponse response = azureDevOpsClient
                .createWorkItem(request, configuration);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(123, response.id());
        Assertions.assertEquals(4, response.rev());
        Assertions.assertEquals("Title", response.fields().title());
        Assertions.assertEquals("Description", response.fields().description());
        Assertions.assertEquals("Impediment", response.fields().workItemType());
        Assertions.assertEquals("New", response.fields().state());
        Assertions.assertEquals(2, response.fields().priority());
        Assertions.assertEquals("https://example.com/workitems/123", response.links().self().href());
        Assertions.assertEquals("https://example.com/workitems/123", response.url());
        Assertions.assertEquals("Carlos Torres", response.fields().createdBy().displayName());
        Assertions.assertEquals("identity-1", response.fields().createdBy().id());
        Assertions.assertEquals("carlos@example.com", response.fields().createdBy().uniqueName());
        Assertions.assertEquals("aad.identity-1", response.fields().createdBy().descriptor());
        Assertions.assertEquals(
                "https://example.com/avatar/1",
                response.fields()
                        .createdBy()
                        .links()
                        .avatar()
                        .href()
        );

        Mockito.verify(httpClient, Mockito.times(1))
                .send(Mockito.any(HttpRequest.class), Mockito.any(HttpResponse.BodyHandler.class));

    }
}
