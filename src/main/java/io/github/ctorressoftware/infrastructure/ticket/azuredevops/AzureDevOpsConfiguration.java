package io.github.ctorressoftware.infrastructure.ticket.azuredevops;

public record AzureDevOpsConfiguration(
        String azureWorkItemType,
        String azureOrganization,
        String azureProject,
        String azurePat) {
    public static String AZURE_BASE_URL = "https://dev.azure.com/";
    public static String AZURE_DOMAIN = "flowprobe";
    public static String AZURE_ACCOUNT = "azure";
}