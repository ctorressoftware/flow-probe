package io.github.ctorressoftware.domain.model;

import java.time.Duration;
import java.util.Objects;

// TODO: Consider adding startedAt and finishedAt fields in the future

public record FlowExecutionSummaryDetail(
    String stepName,
    boolean successful,
    ServiceCall executed,
    Duration executionDuration,
    ResponseValidationResult validationResult,
    String rawResponse
) {
    public FlowExecutionSummaryDetail {
        Objects.requireNonNull(stepName);
        Objects.requireNonNull(executed);
        Objects.requireNonNull(executionDuration);
        Objects.requireNonNull(validationResult);
    }

    public static FlowExecutionSummaryDetail success(
            String stepName,
            ServiceCall executed,
            Duration executionDuration,
            ResponseValidationResult validationResult,
            String rawResponse
    ) {
        return new FlowExecutionSummaryDetail(
                stepName,
                true,
                executed,
                executionDuration,
                validationResult,
                rawResponse
        );
    }

    public static FlowExecutionSummaryDetail failure(
            String stepName,
            ServiceCall executed,
            Duration executionDuration,
            ResponseValidationResult validationResult,
            String rawResponse
    ) {
        return new FlowExecutionSummaryDetail(
                stepName,
                false,
                executed,
                executionDuration,
                validationResult,
                rawResponse
        );
    }
}