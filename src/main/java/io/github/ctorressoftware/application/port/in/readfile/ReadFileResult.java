package io.github.ctorressoftware.application.port.in.readfile;

import io.github.ctorressoftware.domain.model.StepFormat;

import java.util.List;

public record ReadFileResult(List<StepFormat> stepFormats) {}
