package io.github.ctorressoftware.domain.model;

public class ExecutionResumeDetail {
    private String stepName;
    private boolean isSuccessful;
    private String responseString;

    public ExecutionResumeDetail() {}

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
