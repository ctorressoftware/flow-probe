package io.github.ctorressoftware.domain.model;

import java.util.List;

public class FlowExecutionSummary {
    private String flowName;
    private boolean successfulExecution;
    private List<FlowExecutionSummaryDetail> stepsResults;

    public FlowExecutionSummary(
            String flowName,
            boolean successfulExecution,
            List<FlowExecutionSummaryDetail> stepsResults
    ) {
        this.flowName = flowName;
        this.successfulExecution = successfulExecution;
        this.stepsResults = stepsResults;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public boolean isSuccessfulExecution() {
        return successfulExecution;
    }

    public void setSuccessfulExecution(boolean successfulExecution) {
        this.successfulExecution = successfulExecution;
    }

    public List<FlowExecutionSummaryDetail> getStepsResults() {
        return stepsResults;
    }

    public void setStepsResults(List<FlowExecutionSummaryDetail> stepsResults) {
        this.stepsResults = stepsResults;
    }
}