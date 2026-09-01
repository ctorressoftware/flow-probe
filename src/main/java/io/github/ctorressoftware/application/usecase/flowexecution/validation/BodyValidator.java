package io.github.ctorressoftware.application.usecase.flowexecution.validation;

import io.github.ctorressoftware.application.port.out.JsonProcessor;
import io.github.ctorressoftware.application.usecase.flowexecution.validation.evaluator.ExpectationEvaluator;
import io.github.ctorressoftware.application.usecase.flowexecution.validation.evaluator.ExpectationEvaluatorRegistry;
import io.github.ctorressoftware.application.usecase.flowexecution.validation.result.ExpectationResult;
import io.github.ctorressoftware.domain.model.BodyExpectation;

import java.util.List;

public final class BodyValidator {
    private final JsonProcessor jsonProcessor;
    private final ExpectationEvaluatorRegistry registry;

    public BodyValidator(JsonProcessor jsonProcessor, ExpectationEvaluatorRegistry registry) {
        this.jsonProcessor = jsonProcessor;
        this.registry = registry;
    }

    public List<ExpectationResult> validate(String responseBody, List<BodyExpectation> expectations) {
        return expectations.stream()
                .map(expectation -> validateExpectation(responseBody, expectation))
                .toList();
    }

    private ExpectationResult validateExpectation(String responseBody, BodyExpectation expectation) {
        Object actual = jsonProcessor.extractValue(responseBody, expectation.path());
        ExpectationEvaluator evaluator = registry.get(expectation.operator());
        return evaluator.evaluate(actual, expectation.expectedValue());
    }
}
