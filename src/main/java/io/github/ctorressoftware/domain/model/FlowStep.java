package io.github.ctorressoftware.domain.model;

import java.util.Map;
import java.util.Objects;

public record FlowStep(
        String flowName,
        String stepName,
        ServiceCall serviceCall,
        Map<String, String> requires,
        Map<String, String> export
) {

    public static FlowStep create(
            String flowName,
            String stepName,
            ServiceCall serviceCall,
            Map<String, String> requires,
            Map<String, String> export) {

        return new FlowStep(
                Objects.requireNonNull(flowName),
                Objects.requireNonNull(stepName),
                Objects.requireNonNull(serviceCall),
                requires == null ? null : Map.copyOf(requires),
                export == null ? null : Map.copyOf(export)
        );
    }
}