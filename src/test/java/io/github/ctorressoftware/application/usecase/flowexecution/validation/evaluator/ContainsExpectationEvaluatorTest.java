package io.github.ctorressoftware.application.usecase.flowexecution.validation.evaluator;

import io.github.ctorressoftware.domain.model.ExpectationOperator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContainsExpectationEvaluatorTest {

    private ContainsExpectationEvaluator evaluator;

    @BeforeEach
    void init() {
        evaluator = new ContainsExpectationEvaluator();
    }

    @Test
    void shouldSupportContainsOperator() {

        Assertions.assertEquals(
                ExpectationOperator.CONTAINS,
                evaluator.operator()
        );
    }
}
