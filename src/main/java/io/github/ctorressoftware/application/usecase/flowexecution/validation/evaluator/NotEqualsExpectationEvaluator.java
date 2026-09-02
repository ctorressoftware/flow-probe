package io.github.ctorressoftware.application.usecase.flowexecution.validation.evaluator;

import io.github.ctorressoftware.application.usecase.flowexecution.validation.result.ExpectationResult;
import io.github.ctorressoftware.domain.model.ExpectationOperator;

import java.util.Objects;

public class NotEqualsExpectationEvaluator implements ExpectationEvaluator {

    @Override
    public ExpectationOperator operator() {
        return ExpectationOperator.NOT_EQUALS;
    }

    @Override
    public ExpectationResult evaluate(Object actual, Object expected) {

        boolean successful = !Objects.equals(actual, expected);

        return new ExpectationResult(
                successful,
                expected,
                actual
        );
    }
}
