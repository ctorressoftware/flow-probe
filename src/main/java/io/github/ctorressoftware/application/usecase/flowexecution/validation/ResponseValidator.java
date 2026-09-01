package io.github.ctorressoftware.application.usecase.flowexecution.validation;

import io.github.ctorressoftware.application.usecase.flowexecution.validation.result.ResponseValidationResult;
import io.github.ctorressoftware.domain.model.CallResult;
import io.github.ctorressoftware.domain.model.ExpectedResponse;

public interface ResponseValidator {

    ResponseValidationResult validate(
            CallResult callResult,
            ExpectedResponse expectedResponse
    );
}
