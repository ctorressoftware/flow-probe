package io.github.ctorressoftware.infrastructure.ticket.azuredevops;

public record AzureDevOpsPatchOperation(
        String op,
        String path,
        Object value
) {
    public static AzureDevOpsPatchOperation add(String path, Object value) {
        return new AzureDevOpsPatchOperation("add", path, value);
    }
}
