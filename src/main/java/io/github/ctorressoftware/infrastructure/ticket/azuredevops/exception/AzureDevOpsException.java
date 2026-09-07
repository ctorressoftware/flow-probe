package io.github.ctorressoftware.infrastructure.ticket.azuredevops.exception;

public class AzureDevOpsException extends RuntimeException {

    public AzureDevOpsException(String message) {
        super(message);
    }

    public AzureDevOpsException(String message, Throwable cause) {
        super(message, cause);
    }
}
