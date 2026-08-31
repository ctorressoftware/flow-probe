package io.github.ctorressoftware.domain.model;

import java.util.Map;
import java.util.Objects;

public record FlowStep(
        String flowName,
        String stepName,
        ServiceCall serviceCall,
        ExpectedResponse expectedResponse,
        Map<String, String> exports
) {

    public static FlowStep create(
            String flowName,
            String stepName,
            ServiceCall serviceCall,
            ExpectedResponse expectedResponse,
            Map<String, String> exports) {

        return new FlowStep(
                Objects.requireNonNull(flowName),
                Objects.requireNonNull(stepName),
                Objects.requireNonNull(serviceCall),
                expectedResponse, // TODO: Can be null yet. Have to think later it should be mandatory or not
                exports == null ? null : Map.copyOf(exports)
        );
    }
}