package io.github.ctorressoftware.domain.model;

import java.time.Duration;
// TODO: Consider adding startedAt and finishedAt fields in the future
public record FlowExecutionSummaryDetail(
    String stepName,
    boolean successful,
    ServiceCall executed,
    Duration executionDuration,
    String rawResponse
) {} // TODO: add validations