package io.github.ctorressoftware.application.usecase.flowexecution.validation;

import io.github.ctorressoftware.application.usecase.flowexecution.validation.result.ExpectationResult;
import io.github.ctorressoftware.application.usecase.flowexecution.validation.result.ResponseValidationResult;
import io.github.ctorressoftware.domain.constant.HttpStatusCode;
import io.github.ctorressoftware.domain.model.BodyExpectation;
import io.github.ctorressoftware.domain.model.CallResult;
import io.github.ctorressoftware.domain.model.ExpectedResponse;
import io.github.ctorressoftware.domain.model.ExpectationOperator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class DefaultResponseValidatorTest {

    @Mock
    private StatusValidator statusValidator;

    @Mock
    private BodyValidator bodyValidator;

    private DefaultResponseValidator responseValidator;

    @BeforeEach
    void init() {
        responseValidator = new DefaultResponseValidator(statusValidator, bodyValidator);
    }

    @Test
    void shouldValidateSuccessfulStatusByDefaultWhenExpectedResponseIsNull() {

        CallResult callResult = new CallResult(
                HttpStatusCode.OK,
                null
        );

        ResponseValidationResult result = responseValidator.validate(callResult, null);

        Assertions.assertTrue(result.successful());
        Assertions.assertEquals(1, result.results().size());
        ExpectationResult expectationResult = result.results().getFirst();
        Assertions.assertTrue(expectationResult.successful());
        Assertions.assertEquals("200-299", expectationResult.expected());
        Assertions.assertEquals(HttpStatusCode.OK, expectationResult.actual());
        Mockito.verifyNoInteractions(statusValidator, bodyValidator);
    }

    @Test
    void shouldFailDefaultValidationWhenStatusIsNotSuccessful() {

        CallResult callResult = new CallResult(
                HttpStatusCode.INTERNAL_SERVER_ERROR,
                null
        );

        ResponseValidationResult result = responseValidator.validate(callResult, null);

        Assertions.assertFalse(result.successful());
        Assertions.assertEquals(1, result.results().size());
        ExpectationResult expectationResult = result.results().getFirst();
        Assertions.assertFalse(expectationResult.successful());
        Assertions.assertEquals("200-299", expectationResult.expected());
        Assertions.assertEquals(HttpStatusCode.INTERNAL_SERVER_ERROR, expectationResult.actual());
        Mockito.verifyNoInteractions(statusValidator, bodyValidator);
    }

    @Test
    void shouldValidateExpectedStatusWhenStatusIsDefined() {

        CallResult callResult = new CallResult(
                HttpStatusCode.CREATED,
                null
        );

        ExpectedResponse expectedResponse = new ExpectedResponse(
                HttpStatusCode.CREATED,
                List.of()
        );

        ExpectationResult statusResult = new ExpectationResult(
                true,
                HttpStatusCode.CREATED,
                HttpStatusCode.CREATED
        );

        Mockito
                .when(statusValidator.validate(HttpStatusCode.CREATED, HttpStatusCode.CREATED))
                .thenReturn(statusResult);

        ResponseValidationResult result = responseValidator.validate(
                callResult,
                expectedResponse
        );

        Assertions.assertTrue(result.successful());
        Assertions.assertEquals(List.of(statusResult), result.results());
        Mockito.verifyNoInteractions(bodyValidator);
        Mockito.verifyNoMoreInteractions(statusValidator);
        Mockito.verify(statusValidator).validate(HttpStatusCode.CREATED, HttpStatusCode.CREATED);
    }

    @Test
    void shouldUseDefaultStatusValidationWhenExpectedStatusIsNull() {

        CallResult callResult = new CallResult(
                HttpStatusCode.ACCEPTED,
                null
        );

        ExpectedResponse expectedResponse = new ExpectedResponse(
                null,
                List.of()
        );

        ResponseValidationResult result = responseValidator.validate(
                callResult,
                expectedResponse
        );

        Assertions.assertTrue(result.successful());
        Assertions.assertEquals(1, result.results().size());
        ExpectationResult expectationResult = result.results().getFirst();
        Assertions.assertTrue(expectationResult.successful());
        Assertions.assertEquals("200-299", expectationResult.expected());
        Assertions.assertEquals(HttpStatusCode.ACCEPTED, expectationResult.actual());
        Mockito.verifyNoInteractions(statusValidator, bodyValidator);
    }

    @Test
    void shouldValidateStatusAndBodyExpectations() {

        String responseBody = """
                {
                  "name": "bulbasaur"
                }
                """;

        CallResult callResult = new CallResult(
                HttpStatusCode.OK,
                responseBody
        );

        BodyExpectation bodyExpectation = new BodyExpectation(
                "/name",
                ExpectationOperator.EQUALS,
                "bulbasaur"
        );

        ExpectedResponse expectedResponse = new ExpectedResponse(
                HttpStatusCode.OK,
                List.of(bodyExpectation)
        );

        ExpectationResult statusResult = new ExpectationResult(
                true,
                HttpStatusCode.OK,
                HttpStatusCode.OK
        );

        ExpectationResult bodyResult = new ExpectationResult(
                true,
                "bulbasaur",
                "bulbasaur"
        );

        Mockito
                .when(statusValidator.validate(HttpStatusCode.OK, HttpStatusCode.OK))
                .thenReturn(statusResult);

        Mockito
                .when(bodyValidator.validate(responseBody, List.of(bodyExpectation)))
                .thenReturn(List.of(bodyResult));

        ResponseValidationResult result = responseValidator.validate(
                callResult,
                expectedResponse
        );

        Assertions.assertTrue(result.successful());
        Assertions.assertEquals(List.of(statusResult, bodyResult), result.results());
        Mockito.verify(statusValidator).validate(HttpStatusCode.OK, HttpStatusCode.OK);
        Mockito.verify(bodyValidator).validate(responseBody, List.of(bodyExpectation));
        Mockito.verifyNoMoreInteractions(statusValidator, bodyValidator);
    }

    @Test
    void shouldReturnFailedValidationWhenBodyExpectationFails() {

        String responseBody = """
                {
                  "name": "pikachu"
                }
                """;

        CallResult callResult = new CallResult(
                HttpStatusCode.OK,
                responseBody
        );

        BodyExpectation bodyExpectation = new BodyExpectation(
                "/name",
                ExpectationOperator.EQUALS,
                "bulbasaur"
        );

        ExpectedResponse expectedResponse = new ExpectedResponse(
                HttpStatusCode.OK,
                List.of(bodyExpectation)
        );

        ExpectationResult statusResult = new ExpectationResult(
                true,
                HttpStatusCode.OK,
                HttpStatusCode.OK
        );

        ExpectationResult bodyResult = new ExpectationResult(
                false,
                "bulbasaur",
                "pikachu"
        );

        Mockito
                .when(statusValidator.validate(HttpStatusCode.OK, HttpStatusCode.OK))
                .thenReturn(statusResult);

        Mockito
                .when(bodyValidator.validate(responseBody, List.of(bodyExpectation)))
                .thenReturn(List.of(bodyResult));

        ResponseValidationResult result = responseValidator.validate(
                callResult,
                expectedResponse
        );

        Assertions.assertFalse(result.successful());
        Assertions.assertEquals(List.of(statusResult, bodyResult), result.results());

        Mockito.verify(statusValidator).validate(
                HttpStatusCode.OK,
                HttpStatusCode.OK
        );

        Mockito.verify(bodyValidator).validate(
                responseBody,
                List.of(bodyExpectation)
        );

        Mockito.verifyNoMoreInteractions(statusValidator, bodyValidator);
    }
}