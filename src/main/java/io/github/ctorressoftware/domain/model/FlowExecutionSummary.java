package io.github.ctorressoftware.domain.model;

import java.util.List;
import java.util.Objects;

public record FlowExecutionSummary(
        String flowName,
        boolean successfulExecution,
        List<FlowExecutionSummaryDetail> stepsResults
) {

    public FlowExecutionSummary(
            String flowName,
            boolean successfulExecution,
            List<FlowExecutionSummaryDetail> stepsResults
    ) {
        this.flowName = Objects.requireNonNull(flowName);
        this.successfulExecution = successfulExecution;
        this.stepsResults = Objects.requireNonNull(stepsResults);
    }
}