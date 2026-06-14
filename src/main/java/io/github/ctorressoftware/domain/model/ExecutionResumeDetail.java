package io.github.ctorressoftware.domain.model;

public class ExecutionResumeDetail {

    private String stepName;
    private boolean isSuccessful;
    private String responseString;

    public ExecutionResumeDetail(
            String stepName,
            boolean isSuccessful,
            String responseString) {
        this.stepName = stepName;
        this.isSuccessful = isSuccessful;
        this.responseString = responseString;
    }

    public static ExecutionResumeDetail create(String stepName) {
        return new ExecutionResumeDetail(stepName, false, null);
    }

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
