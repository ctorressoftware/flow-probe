package io.github.ctorressoftware.application.usecase.flowexecution.validation;

import io.github.ctorressoftware.application.usecase.flowexecution.validation.evaluator.ExpectationEvaluator;
import io.github.ctorressoftware.application.usecase.flowexecution.validation.evaluator.ExpectationEvaluatorRegistry;
import io.github.ctorressoftware.application.usecase.flowexecution.validation.result.ExpectationResult;
import io.github.ctorressoftware.domain.model.ExpectationOperator;

public final class StatusValidator {

    private final ExpectationEvaluatorRegistry registry;

    public StatusValidator(ExpectationEvaluatorRegistry registry) {
        this.registry = registry;
    }

    public ExpectationResult validate(int actualStatus, Integer expectedStatus) {

        ExpectationEvaluator evaluator = registry.get(ExpectationOperator.EQUALS);

        return evaluator.evaluate(
                actualStatus,
                expectedStatus
        );
    }
}