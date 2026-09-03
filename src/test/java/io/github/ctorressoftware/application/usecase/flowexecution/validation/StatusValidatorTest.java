package io.github.ctorressoftware.application.usecase.flowexecution.validation;

import io.github.ctorressoftware.application.usecase.flowexecution.validation.evaluator.ExpectationEvaluator;
import io.github.ctorressoftware.application.usecase.flowexecution.validation.evaluator.ExpectationEvaluatorRegistry;
import io.github.ctorressoftware.application.usecase.flowexecution.validation.result.ExpectationResult;
import io.github.ctorressoftware.domain.constant.HttpStatusCode;
import io.github.ctorressoftware.domain.model.ExpectationOperator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StatusValidatorTest {

    @Mock
    private ExpectationEvaluatorRegistry registry;

    @Mock
    private ExpectationEvaluator evaluator;

    private StatusValidator statusValidator;

    @BeforeEach
    void init() {
        statusValidator = new StatusValidator(registry);
    }

    @Test
    void shouldValidateStatusUsingEqualsEvaluator() {

        int actualStatus = HttpStatusCode.OK;
        Integer expectedStatus = HttpStatusCode.OK;

        ExpectationResult expectedResult = new ExpectationResult(
                true,
                expectedStatus,
                actualStatus
        );

        Mockito
                .when(registry.get(ExpectationOperator.EQUALS))
                .thenReturn(evaluator);

        Mockito
                .when(evaluator.evaluate(actualStatus, expectedStatus))
                .thenReturn(expectedResult);

        ExpectationResult result = statusValidator.validate(
                actualStatus,
                expectedStatus
        );

        Assertions.assertSame(expectedResult, result);
        Mockito.verify(registry).get(ExpectationOperator.EQUALS);
        Mockito.verify(evaluator).evaluate(actualStatus, expectedStatus);
        Mockito.verifyNoMoreInteractions(registry, evaluator);
    }
}