package io.github.ctorressoftware.domain.model;

import java.util.List;

public class ExecutionResume {
    private String flowName;
    private boolean successfulExecution;
    private List<ExecutionResumeDetail> stepsResults;

    public ExecutionResume(
            String flowName,
            boolean successfulExecution,
            List<ExecutionResumeDetail> stepsResults
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

    public List<ExecutionResumeDetail> getStepsResults() {
        return stepsResults;
    }

    public void setStepsResults(List<ExecutionResumeDetail> stepsResults) {
        this.stepsResults = stepsResults;
    }
}