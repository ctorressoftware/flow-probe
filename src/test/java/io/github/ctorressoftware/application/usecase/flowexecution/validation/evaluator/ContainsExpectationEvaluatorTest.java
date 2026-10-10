package io.github.ctorressoftware.application.usecase.flowexecution.validation.evaluator;

import io.github.ctorressoftware.domain.model.ExpectationOperator;
import io.github.ctorressoftware.domain.model.ExpectationResult;
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

    @Test
    void shouldReturnSuccessfulResultWhenStringContainsSubString() {

        ExpectationResult result = evaluator.evaluate(
                "mega-bulbasaur",
                "bulbasaur"
        );

        Assertions.assertTrue(result.successful());
    }

    @Test
    void shouldReturnFailedResultWhenSentenceNotContainsSubString() {

        ExpectationResult result = evaluator.evaluate(
                "pikachu",
                "bulbasaur"
        );

        Assertions.assertFalse(result.successful());
        Assertions.assertEquals("bulbasaur", result.expected());
        Assertions.assertEquals("pikachu", result.actual());
    }
}
