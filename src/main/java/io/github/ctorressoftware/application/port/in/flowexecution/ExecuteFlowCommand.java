package io.github.ctorressoftware.application.port.in.flowexecution;

import io.github.ctorressoftware.domain.model.Flow;

public record ExecuteFlowCommand(Flow flow) {}
