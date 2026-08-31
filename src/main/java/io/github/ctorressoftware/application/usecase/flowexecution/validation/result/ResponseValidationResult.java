package io.github.ctorressoftware.application.usecase.flowexecution.validation.result;

import java.util.List;

public record ResponseValidationResult(boolean successful, List<ExpectationResult> results) {

    public ResponseValidationResult {
        results = List.copyOf(results);
    }

    public static ResponseValidationResult from(List<ExpectationResult> results) {
        boolean successful = results.stream()
                .allMatch(ExpectationResult::successful);

        return new ResponseValidationResult(successful, results);
    }
}
