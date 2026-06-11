package io.github.ctorressoftware.domain.model;

import io.github.ctorressoftware.domain.exception.FlowStepNotStartedBeforeException;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public class FlowStep {
    private final String flowName;
    private final String stepName;
    private final RequestFormat request;
    private final Map<String, Object> expect;
    private final Map<String, String> export;
    private Instant startedAt;
    private Instant finishedAt;

    private FlowStep(
            String flowName,
            String stepName,
            RequestFormat request,
            Map<String, Object> expect,
            Map<String, String> export
    ) {
        this.flowName = Objects.requireNonNull(flowName, "flowName cannot be null");
        this.stepName = Objects.requireNonNull(stepName, "stepName cannot be null");
        this.request = Objects.requireNonNull(request, "request cannot be null");
        this.expect = Map.copyOf(Objects.requireNonNull(expect, "expect cannot be null"));
        this.export = Map.copyOf(Objects.requireNonNull(export, "export cannot be null"));
    }

    public static FlowStep create(
            String flowName,
            String stepName,
            RequestFormat request,
            Map<String, Object> expect,
            Map<String, String> export) {

        return new FlowStep(flowName, stepName, request, expect, export);
    }

    public void start() {
        this.startedAt = Instant.now();
    }

    public void finish() {
        if (startedAt == null) {
            throw new FlowStepNotStartedBeforeException(stepName);
        }

        finishedAt = Instant.now();
    }

    public String getFlowName() {
        return flowName;
    }

    public String getStepName() {
        return stepName;
    }

    public RequestFormat getRequest() {
        return request;
    }

    public Map<String, Object> getExpect() {
        return expect;
    }

    public Map<String, String> getExport() {
        return export;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getFinishedAt() {
        return finishedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FlowStep flowStep)) return false;

        return Objects.equals(flowName, flowStep.flowName)
                && Objects.equals(stepName, flowStep.stepName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flowName, stepName);
    }
}