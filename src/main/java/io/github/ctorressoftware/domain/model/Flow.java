package io.github.ctorressoftware.domain.model;

import java.util.List;
import java.util.Objects;

public class Flow {
    private final String name;
    private final List<FlowStep> steps;

    private Flow(String name, List<FlowStep> steps) {
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.steps = Objects.requireNonNull(steps, "steps cannot be null");
    }

    public static Flow create(String name, List<FlowStep> steps) {
        return new Flow(name, steps);
    }

    public String getName() {
        return name;
    }

    public List<FlowStep> getSteps() {
        return steps;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Flow flow = (Flow) o;
        return Objects.equals(name, flow.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}