package io.github.ctorressoftware.infrastructure.provider.azure;

import io.github.ctorressoftware.application.port.in.provider.configure.ProviderStatus;
import io.github.ctorressoftware.application.port.out.ProviderConfigurator;

import java.util.Scanner;

public class AzureProviderConfigurator implements ProviderConfigurator {

    private final Scanner scanner;

    public AzureProviderConfigurator(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public void configure() {
        System.out.print("Organization: ");
        String azureOrganization = scanner.nextLine();
        System.out.println();

        System.out.print("Project: ");
        String azureProject = scanner.nextLine();
        System.out.println();

        System.out.print("PAT: ");
        String azurePAT = scanner.nextLine();
        System.out.println();

        System.out.println("---------");
        System.out.println("Organization: " + azureOrganization);
        System.out.println("Project: " + azureProject);
        System.out.println("PAT: " + azurePAT);
    }

    @Override
    public void remove() {

    }

    @Override
    public ProviderStatus status() {
        return null;
    }
}
