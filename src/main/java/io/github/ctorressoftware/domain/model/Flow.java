package io.github.ctorressoftware.domain.model;

import java.util.List;

public record Flow(String name, List<FlowStep> steps) {

    public static Flow create(String name, List<FlowStep> steps) {
        return new Flow(name, steps);
    }
}