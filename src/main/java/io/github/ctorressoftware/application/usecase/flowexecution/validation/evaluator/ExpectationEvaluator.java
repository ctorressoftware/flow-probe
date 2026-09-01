package io.github.ctorressoftware.application.usecase.flowexecution.validation.evaluator;

import io.github.ctorressoftware.application.usecase.flowexecution.validation.result.ExpectationResult;
import io.github.ctorressoftware.domain.model.ExpectationOperator;

public interface ExpectationEvaluator {

    ExpectationOperator operator();

    ExpectationResult evaluate(
            Object actual,
            Object expected
    );
}
