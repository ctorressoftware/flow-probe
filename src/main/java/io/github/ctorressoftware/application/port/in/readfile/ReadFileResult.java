package io.github.ctorressoftware.application.port.in.readfile;

import io.github.ctorressoftware.domain.model.FlowStep;

import java.util.List;

public record ReadFileResult(List<FlowStep> flowSteps) {}
