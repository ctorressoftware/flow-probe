package io.github.ctorressoftware.application.usecase.flowexecution.validation.evaluator;

import io.github.ctorressoftware.domain.model.ExpectationOperator;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ExpectationEvaluatorRegistry {

    private final Map<ExpectationOperator, ExpectationEvaluator> evaluators;

    public ExpectationEvaluatorRegistry(List<ExpectationEvaluator> evaluators) {
        this.evaluators = evaluators.stream()
                .collect(Collectors.toUnmodifiableMap(
                        ExpectationEvaluator::operator,
                        Function.identity()
                ));
    }

    public ExpectationEvaluator get(ExpectationOperator operator) {

        ExpectationEvaluator evaluator = evaluators.get(operator);

        if (evaluator == null) {
            throw new IllegalArgumentException("No evaluator registered for operator: " + operator);
        }

        return evaluator;
    }
}
