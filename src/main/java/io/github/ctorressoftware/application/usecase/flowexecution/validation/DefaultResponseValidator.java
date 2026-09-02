package io.github.ctorressoftware.application.usecase.flowexecution.validation;

import io.github.ctorressoftware.application.usecase.flowexecution.validation.result.ExpectationResult;
import io.github.ctorressoftware.application.usecase.flowexecution.validation.result.ResponseValidationResult;
import io.github.ctorressoftware.domain.constant.HttpStatusCode;
import io.github.ctorressoftware.domain.model.CallResult;
import io.github.ctorressoftware.domain.model.ExpectedResponse;

import java.util.ArrayList;
import java.util.List;

public final class DefaultResponseValidator implements ResponseValidator {
    private static final String SUCCESS_STATUS_RANGE = "200-299";
    private final StatusValidator statusValidator;
    private final BodyValidator bodyValidator;

    public DefaultResponseValidator(StatusValidator statusValidator, BodyValidator bodyValidator) {
        this.statusValidator = statusValidator;
        this.bodyValidator = bodyValidator;
    }

    @Override
    public ResponseValidationResult validate(CallResult callResult, ExpectedResponse expectedResponse) {

        if (expectedResponse == null) {
            return ResponseValidationResult.from(
                    List.of(validateDefaultStatus(callResult))
            );
        }

        List<ExpectationResult> results = new ArrayList<>();
        validateStatus(callResult, expectedResponse, results);
        validateBody(callResult, expectedResponse, results);

        return ResponseValidationResult.from(results);
    }

    private void validateStatus(
            CallResult callResult,
            ExpectedResponse expectedResponse,
            List<ExpectationResult> results
    ) {
        if (expectedResponse.status() != null) {
            results.add(statusValidator.validate(
                    callResult.statusCode(),
                    expectedResponse.status())
            );
            return;
        }

        results.add(validateDefaultStatus(callResult));
    }

    private void validateBody(
            CallResult callResult,
            ExpectedResponse expectedResponse,
            List<ExpectationResult> results
    ) {
        if (expectedResponse.bodyExpectations().isEmpty()) {
            return;
        }

        results.addAll(
                bodyValidator.validate(
                        callResult.responseBody(),
                        expectedResponse.bodyExpectations()
                )
        );
    }

    private ExpectationResult validateDefaultStatus(CallResult callResult) {
        return new ExpectationResult(
                HttpStatusCode.isSuccess(callResult.statusCode()),
                SUCCESS_STATUS_RANGE,
                callResult.statusCode()
        );
    }
}
