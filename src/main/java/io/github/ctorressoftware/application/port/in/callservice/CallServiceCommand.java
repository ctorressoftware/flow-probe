package io.github.ctorressoftware.application.port.in.callservice;

import io.github.ctorressoftware.domain.model.ServiceCall;

public record CallServiceCommand(
    ServiceCall serviceCall
) {}
