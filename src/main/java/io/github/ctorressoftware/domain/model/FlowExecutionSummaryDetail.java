package io.github.ctorressoftware.domain.model;

import io.github.ctorressoftware.application.usecase.flowexecution.validation.result.ResponseValidationResult;

import java.time.Duration;

// TODO: Consider adding startedAt and finishedAt fields in the future
// TODO: add validations

public record FlowExecutionSummaryDetail(
    String stepName,
    boolean successful,
    ServiceCall executed,
    Duration executionDuration,
    ResponseValidationResult validationResult,
    String rawResponse
) {

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