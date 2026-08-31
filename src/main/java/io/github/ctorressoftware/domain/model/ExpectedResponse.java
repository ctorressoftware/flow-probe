package io.github.ctorressoftware.domain.model;

import java.util.List;

public record ExpectedResponse(
        Integer status,
        List<BodyExpectation> bodyExpectations
) {}