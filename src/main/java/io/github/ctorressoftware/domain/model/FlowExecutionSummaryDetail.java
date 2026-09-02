package io.github.ctorressoftware.domain.model;

import java.time.Duration;

// TODO: Consider adding startedAt and finishedAt fields in the future
// TODO: add validations
// TODO: Include expectation validation results in the execution summary

public record FlowExecutionSummaryDetail(
    String stepName,
    boolean successful,
    ServiceCall executed,
    Duration executionDuration,
    String rawResponse
) {

    public static FlowExecutionSummaryDetail success(
            String stepName,
            ServiceCall executed,
            Duration executionDuration,
            String rawResponse
    ) {
        return new FlowExecutionSummaryDetail(
                stepName,
                true,
                executed,
                executionDuration,
                rawResponse
        );
    }

    public static FlowExecutionSummaryDetail failure(
            String stepName,
            ServiceCall executed,
            Duration executionDuration,
            String rawResponse
    ) {
        return new FlowExecutionSummaryDetail(
                stepName,
                false,
                executed,
                executionDuration,
                rawResponse
        );
    }

}