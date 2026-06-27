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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// TODO: This is a temporary logic to test Azure DevOps API. Refactor this later.
public class AzureDevOpsWorkItemClient {

    private final HttpClient client;

    public AzureDevOpsWorkItemClient() {
        this.client = HttpClient.newHttpClient();
    }

    public String createWorkItem(AzureDevOpsCreateWorkItemRequest request) {

        ObjectMapper mapper = new ObjectMapper();

        String endpoint = AzureSettings.AZURE_ORGANIZATION + "/" + AzureSettings.AZURE_PROJECT 
            + "/_apis/wit/workitems/$Issue?api-version=7.2-preview.3";

        try {
        
            URI uri = URI.create(AzureSettings.BASE_URL + "/" + endpoint);

            String jsonBody = mapper.writeValueAsString(request.operations());

            String base64Credentials = Base64
                .getEncoder()
                .encodeToString((":" + AzureSettings.AZURE_PAT).getBytes(StandardCharsets.UTF_8));
            
            HttpRequest httpRequest = HttpRequest
                .newBuilder(uri)
                .POST(BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json-patch+json")
                .header("Authorization", "Basic " + base64Credentials)
                .build();
            
            HttpResponse<String> response = client
                .send(httpRequest, BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    System.out.println();
                    throw new RuntimeException(response.body());
                }

                return response.body();

        } catch(JsonProcessingException e) {
            throw new RuntimeException("error");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("error");
        }
    }
}
