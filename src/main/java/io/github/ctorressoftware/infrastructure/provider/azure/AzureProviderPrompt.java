package io.github.ctorressoftware.infrastructure.provider.azure;

import io.github.ctorressoftware.application.port.out.ProviderPrompt;

import java.util.Map;
import java.util.Scanner;

public class AzureProviderPrompt implements ProviderPrompt {

    private final Scanner scanner;

    public AzureProviderPrompt(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public Map<String, String> prompt() {

        String organization = askFor("Write your Azure DevOps Organization: ");
        String project = askFor("Write your Azure DevOps project: ");
        String pat = askForSecret("Write your Azure DevOps Personal Access Token (PAT): ");

        return Map.of(
                "organization", organization,
                "project", project,
                "pat", pat
        );
    }

    private String askFor(String something) { // TODO: move ask methods to a different file like ProviderDataAsker
        System.out.print(something);
        return scanner.nextLine();
    }

    private String askForSecret(String promptMessage) {
        java.io.Console console = System.console();
        if (console != null) {
            char[] passwordChars = console.readPassword(promptMessage);
            return new String(passwordChars);
        } else {
            System.out.print(promptMessage);
            return scanner.nextLine();
        }
    }
}
