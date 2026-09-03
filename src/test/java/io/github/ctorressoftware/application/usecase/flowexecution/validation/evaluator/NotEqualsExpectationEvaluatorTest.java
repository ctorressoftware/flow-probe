package io.github.ctorressoftware.application.usecase.flowexecution.validation.evaluator;

import io.github.ctorressoftware.application.usecase.flowexecution.validation.result.ExpectationResult;
import io.github.ctorressoftware.domain.model.ExpectationOperator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NotEqualsExpectationEvaluatorTest {

    private NotEqualsExpectationEvaluator evaluator;

    @BeforeEach
    void init() {
        evaluator = new NotEqualsExpectationEvaluator();
    }

    @Test
    void shouldSupportNotEqualsOperator() {
        Assertions.assertEquals(ExpectationOperator.NOT_EQUALS, evaluator.operator());
    }

    @Test
    void shouldReturnSuccessfulResultWhenValuesAreDifferent() {

        ExpectationResult result = evaluator.evaluate(
                "pikachu",
                "bulbasaur"
        );

        Assertions.assertTrue(result.successful());
        Assertions.assertEquals("bulbasaur", result.expected());
        Assertions.assertEquals("pikachu", result.actual());
    }

    @Test
    void shouldReturnFailedResultWhenValuesAreEqual() {

        ExpectationResult result = evaluator.evaluate(
                "bulbasaur",
                "bulbasaur"
        );

        Assertions.assertFalse(result.successful());
        Assertions.assertEquals("bulbasaur", result.expected());
        Assertions.assertEquals("bulbasaur", result.actual());
    }

    @Test
    void shouldReturnFailedResultWhenBothValuesAreNull() {

        ExpectationResult result = evaluator.evaluate(null, null);

        Assertions.assertFalse(result.successful());
        Assertions.assertNull(result.expected());
        Assertions.assertNull(result.actual());
    }

    @Test
    void shouldReturnSuccessfulResultWhenOnlyActualValueIsNull() {

        ExpectationResult result = evaluator.evaluate(null, "bulbasaur");

        Assertions.assertTrue(result.successful());
        Assertions.assertEquals("bulbasaur", result.expected());
        Assertions.assertNull(result.actual());
    }

    @Test
    void shouldReturnSuccessfulResultWhenOnlyExpectedValueIsNull() {

        ExpectationResult result = evaluator.evaluate("bulbasaur", null);

        Assertions.assertTrue(result.successful());
        Assertions.assertNull(result.expected());
        Assertions.assertEquals("bulbasaur", result.actual());
    }
}