package io.github.ctorressoftware.infrastructure.provider.azure;

import io.github.ctorressoftware.application.port.out.ProviderConfig;
import io.github.ctorressoftware.application.port.out.ProviderPrompt;
import io.github.ctorressoftware.infrastructure.ticket.azuredevops.AzureDevOpsConfig;

import java.io.Console;
import java.io.PrintStream;
import java.util.Scanner;

public class AzureProviderPrompt implements ProviderPrompt {

    private final PrintStream out;
    private final Scanner scanner;
    private final Console console;

    public AzureProviderPrompt(PrintStream out, Scanner scanner, Console console) {
        this.out = out;
        this.scanner = scanner;
        this.console = console;
    }

    @Override
    public ProviderConfig prompt() {

        String organization = askFor("Write your Azure DevOps Organization: ");
        String project = askFor("Write your Azure DevOps project: ");
        String workItemType = askFor("Write your Azure DevOps Work Item Type: ");
        String pat = askForSecret("Write your Azure DevOps Personal Access Token (PAT): ");

        return new AzureDevOpsConfig(
                organization,
                project,
                workItemType,
                pat
        );
    }

    private String askFor(String something) { // TODO: move ask methods to a different file like ProviderDataAsker
        out.print(something);
        return scanner.nextLine();
    }

    /*
     * TODO: Avoid converting the secret from char[] to String, since String remains in memory
     * until garbage collection. Refactor the related interfaces to use char[] where possible.
     */
    private String askForSecret(String promptMessage) {
        if (console != null) {
            char[] passwordChars = console.readPassword(promptMessage);
            return new String(passwordChars);
        } else {
            out.print(promptMessage);
            return scanner.nextLine();
        }
    }
}
