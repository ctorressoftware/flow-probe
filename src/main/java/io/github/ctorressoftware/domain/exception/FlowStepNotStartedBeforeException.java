package io.github.ctorressoftware.domain.exception;

public class FlowStepNotStartedBeforeException extends RuntimeException {
    public FlowStepNotStartedBeforeException(String stepName) {
        super("Step cannot be finished without started before. Name: " + stepName);
    }
}
