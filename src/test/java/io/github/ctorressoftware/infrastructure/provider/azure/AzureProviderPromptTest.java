package io.github.ctorressoftware.infrastructure.provider.azure;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.PrintStream;
import java.util.Map;
import java.util.Scanner;

@ExtendWith(MockitoExtension.class)
class AzureProviderPromptTest {

    @Mock
    private PrintStream out;

    @Mock
    private Scanner scanner;

    private AzureProviderPrompt azureProviderPrompt;

    @BeforeEach
    void init() {
        azureProviderPrompt = new AzureProviderPrompt(out, scanner);
    }

    @Test
    void testAzurePromptData() {

        Map<String, String> azureTestData = Map.of(
                "organization", "test",
                "project", "test",
                "workItemType", "test",
                "pat", "test"
        );

        Mockito.when(scanner.nextLine()).thenReturn("test");

        Map<String, String> data = azureProviderPrompt.prompt();

        Assertions.assertEquals(azureTestData, data);
    }

}
