package io.github.ctorressoftware.infrastructure.readfile.yaml;

import io.github.ctorressoftware.domain.model.*;

import java.util.List;

public final class YamlFlowMapper {

    public Flow map(YamlFlow yamlFlow) {
        var steps = yamlFlow.getSteps()
                .stream()
                .map(step -> mapStep(yamlFlow.getName(), step))
                .toList();

        return Flow.create(
                yamlFlow.getName(),
                steps
        );
    }

    private FlowStep mapStep(String flowName, YamlStep yamlStep) {
        return FlowStep.create(
                flowName,
                yamlStep.getName(),
                mapServiceCall(yamlStep.getRequest()),
                mapExpectedResponse(yamlStep.getExpect()),
                yamlStep.getExports()
        );
    }

    private ServiceCall mapServiceCall(YamlStepRequest yamlServiceCall) {
        return new ServiceCall(
                yamlServiceCall.getUrl(),
                yamlServiceCall.getMethod(),
                yamlServiceCall.getHeaders(),
                yamlServiceCall.getBody()
        );
    }

    private ExpectedResponse mapExpectedResponse(YamlExpectations yamlExpectations) {
        if (yamlExpectations == null) return null;

        var bodyExpectations = yamlExpectations.getBody() == null
                ? List.<BodyExpectation>of()
                : yamlExpectations.getBody()
                .stream()
                .map(this::mapBodyExpectation)
                .toList();

        return new ExpectedResponse(
                yamlExpectations.getStatus(),
                bodyExpectations
        );
    }

    private BodyExpectation mapBodyExpectation(YamlBodyExpectation yamlExpectation) {
        return new BodyExpectation(
                yamlExpectation.getPath(),
                ExpectationOperator.from(
                        yamlExpectation.getOperator()
                ),
                yamlExpectation.getValue()
        );
    }
}
