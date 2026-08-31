package io.github.ctorressoftware.application.usecase.flowexecution.validation.result;

public record ExpectationResult(
        boolean successful,
        Object expected,
        Object actual
) {}