package io.github.ctorressoftware.domain.model;

import java.util.Map;

public record StepFormat(
        String flowName,
        String stepName,
        RequestFormat request,
        Map<String, Object> expect,
        Map<String, String> export
) {}