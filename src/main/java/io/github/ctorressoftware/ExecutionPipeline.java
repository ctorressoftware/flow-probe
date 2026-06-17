package io.github.ctorressoftware;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ctorressoftware.domain.model.*;
import io.github.ctorressoftware.domain.exception.NoDefinedStepsException;
import io.github.ctorressoftware.infrastructure.callservice.RestServiceCaller;
import io.github.ctorressoftware.infrastructure.context.ExecutionContext;

import java.util.List;
import java.util.Objects;

public class ExecutionPipeline {

    private final RestServiceCaller restServiceCaller;
    private final ExecutionContext context;

    public ExecutionPipeline() {
        this.restServiceCaller = new RestServiceCaller();
        this.context = new ExecutionContext();
    }

    public ExecutionResume execute(String flowName, List<FlowStep> steps) {

        ExecutionResume executionResume = ExecutionResume.create(flowName);

        if (steps == null || steps.isEmpty())
            throw new NoDefinedStepsException();

        steps.forEach(step -> {

            ExecutionResumeDetail stepDetails = ExecutionResumeDetail.create(step.getStepName());
            ServiceCall request = step.getServiceCall();

            if (step.getExpect() != null) {
                request = resolvePlaceholders(request);
            }

            var response = restServiceCaller.call(new ServiceCall(
                    request.url(),
                    request.method(),
                    request.headers(),
                    request.body()
            ));

            if (response == null || response.status() != 200) {
                stepDetails.setSuccessful(false);
                stepDetails.setResponseString(null);
            }

            String jsonResponse = Objects.requireNonNull(response).responseBody();

            if (step.getExport() != null) {

                ObjectMapper mapper = new ObjectMapper();

                try {
                    JsonNode root = mapper.readTree(jsonResponse);
                    step.getExport().forEach((key, keyValue) -> {
                        String valuePath = "/" + keyValue.replace(".", "/");
                        String value = root.at(valuePath).asText();
                        context.putVariable(key, value);
                    });

                    stepDetails.setSuccessful(true);
                    stepDetails.setResponseString(jsonResponse);
                    executionResume.getStepsResults().add(stepDetails);

                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return executionResume;
    }

    private ServiceCall resolvePlaceholders(ServiceCall request) {

        return new ServiceCall(
                context.resolvePlaceholders(request.url()),
                request.method(),
                request.headers(),
                context.resolvePlaceholders(request.body().toString())
        );
    }
}
