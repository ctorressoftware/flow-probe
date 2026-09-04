package io.github.ctorressoftware.application.usecase.flowexecution.validation.evaluator;

import io.github.ctorressoftware.domain.model.ExpectationOperator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class ExpectationEvaluatorRegistryTest {

    @Test
    void shouldReturnRegisteredEvaluator() {

        EqualsExpectationEvaluator equalsEvaluator = new EqualsExpectationEvaluator();
        NotEqualsExpectationEvaluator notEqualsEvaluator = new NotEqualsExpectationEvaluator();

        ExpectationEvaluatorRegistry registry = new ExpectationEvaluatorRegistry(List.of(
                equalsEvaluator,
                notEqualsEvaluator
        ));

        ExpectationEvaluator evaluator = registry.get(ExpectationOperator.EQUALS);
        Assertions.assertSame(equalsEvaluator, evaluator);
    }

    @Test
    void shouldThrowWhenNoEvaluatorIsRegisteredForOperator() {

        ExpectationEvaluatorRegistry registry =
                new ExpectationEvaluatorRegistry(List.of(new EqualsExpectationEvaluator()));

        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> registry.get(
                        ExpectationOperator.NOT_EQUALS
                )
        );

        Assertions.assertEquals(
                "No evaluator registered for operator: NOT_EQUALS",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectDuplicateEvaluatorsForSameOperator() {

        Assertions.assertThrows(
                IllegalStateException.class,
                () -> new ExpectationEvaluatorRegistry(List.of(
                        new EqualsExpectationEvaluator(),
                        new EqualsExpectationEvaluator()
                ))
        );
    }
}