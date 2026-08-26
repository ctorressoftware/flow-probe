package io.github.ctorressoftware.infrastructure.ticket.azuredevops;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AzureDevOpsPatchOperationTest {

    @Test
    void shouldAddAzureDevOpsPatchOperationData() {

        Object content = new Object();

        AzureDevOpsPatchOperation patchOperation = 
            AzureDevOpsPatchOperation.add("/azuredevops/path/", content);

        AzureDevOpsPatchOperation expected = new AzureDevOpsPatchOperation(
            "add", 
            "/azuredevops/path/", 
            content
        );

        Assertions.assertEquals(expected, patchOperation);
    }
}
