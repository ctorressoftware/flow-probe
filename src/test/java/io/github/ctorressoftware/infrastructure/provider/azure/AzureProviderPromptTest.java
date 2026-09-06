package io.github.ctorressoftware.infrastructure.provider.azure;

import io.github.ctorressoftware.application.port.out.ProviderConfig;
import io.github.ctorressoftware.infrastructure.ticket.azuredevops.AzureDevOpsConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.Console;
import java.io.PrintStream;
import java.util.Map;
import java.util.Scanner;

@ExtendWith(MockitoExtension.class)
class AzureProviderPromptTest {

    @Mock
    private PrintStream out;

    @Mock
    private Scanner scanner;

    @Mock
    private Console console;

    private static final String PAT_PROMPT =
            "Write your Azure DevOps Personal Access Token (PAT): ";

    private AzureProviderPrompt azureProviderPrompt;

    @Test
    void shouldReadAllValuesFromScannerWhenConsoleIsUnavailable() {

        azureProviderPrompt = new AzureProviderPrompt(out, scanner, null);

        AzureDevOpsConfig expected = new AzureDevOpsConfig(
                "test",
                "test",
                "test",
                "test"
        );

        Mockito.when(scanner.nextLine()).thenReturn("test");

        ProviderConfig azureConfig = azureProviderPrompt.prompt();

        Assertions.assertEquals(expected, azureConfig);

        Mockito.verify(scanner, Mockito.times(4)).nextLine();
        Mockito.verifyNoInteractions(console);
    }

    @Test
    void shouldReadPatFromConsoleWhenConsoleIsAvailable() {

        azureProviderPrompt = new AzureProviderPrompt(out, scanner, console);

        AzureDevOpsConfig expected = new AzureDevOpsConfig(
                "test",
                "test",
                "test",
                "test"
        );

        Mockito.when(scanner.nextLine()).thenReturn("test");

        Mockito
                .when(console.readPassword(PAT_PROMPT))
                .thenReturn("test".toCharArray());

        ProviderConfig azureConfig = azureProviderPrompt.prompt();

        Assertions.assertEquals(expected, azureConfig);

        Mockito.verify(scanner, Mockito.times(3)).nextLine();
        Mockito.verify(console, Mockito.times(1)).readPassword(PAT_PROMPT);
        Mockito.verifyNoMoreInteractions(scanner, console);
    }
}
