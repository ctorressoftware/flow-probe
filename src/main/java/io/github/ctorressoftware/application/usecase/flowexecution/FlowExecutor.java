package io.github.ctorressoftware.application.usecase.flowexecution;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ctorressoftware.application.port.out.ServiceCaller;
import io.github.ctorressoftware.domain.exception.NoDefinedStepsException;
import io.github.ctorressoftware.domain.model.*;

import java.util.List;
import java.util.Objects;

/* TODO: migrate ExecutionPipeline logic to this file
 * Set ExecutionPipeline as an process orchestrator and delegate other logics to helper files
 */
public class FlowExecutor {
    private final Context context;
    private final ServiceCaller serviceCaller;

    public FlowExecutor(Context context, ServiceCaller serviceCaller) {
        this.context = context;
        this.serviceCaller = serviceCaller;
    }

    public ExecutionResume execute(Flow flow) {

        if (flow == null)
            throw new RuntimeException("No flow to execute in the file"); // Define a custom exception

        if (flow.getSteps() == null || flow.getSteps().isEmpty())
            throw new NoDefinedStepsException();

        ExecutionResume executionResume = ExecutionResume.create(flow.getName());

        flow.getSteps().forEach(step -> {

            ExecutionResumeDetail stepDetails = ExecutionResumeDetail.create(step.getStepName());
            ServiceCall request = step.getServiceCall();

            if (step.getExpect() != null) {
                request = resolvePlaceholders(request);
            }

            var response = serviceCaller.call(new ServiceCall(
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

        // TODO: adapt ServiceCall context methods
        return new ServiceCall(
                context.resolvePlaceholders(request.url()),
                request.method(),
                request.headers(),
                context.resolvePlaceholders(request.body().toString())
        );
    }
}
