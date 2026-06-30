package io.github.ctorressoftware.domain.model;

public class FlowExecutionSummaryDetail {
    private String stepName;
    private boolean isSuccessful;
    private String responseString;

    public FlowExecutionSummaryDetail() {}

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public void setSuccessful(boolean successful) {
        isSuccessful = successful;
    }

    public String getResponseString() {
        return responseString;
    }

    public void setResponseString(String responseString) {
        this.responseString = responseString;
    }
}
