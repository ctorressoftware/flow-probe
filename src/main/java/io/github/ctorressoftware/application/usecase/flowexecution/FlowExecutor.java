package io.github.ctorressoftware.application.usecase.flowexecution;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ctorressoftware.application.port.out.ServiceCaller;
import io.github.ctorressoftware.domain.constant.HttpStatusCode;
import io.github.ctorressoftware.domain.exception.NoDefinedFlowException;
import io.github.ctorressoftware.domain.exception.NoDefinedStepsException;
import io.github.ctorressoftware.domain.model.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class FlowExecutor {
    private final Context context;
    private final ServiceCaller serviceCaller;

    public FlowExecutor(Context context, ServiceCaller serviceCaller) {
        this.context = context;
        this.serviceCaller = serviceCaller;
    }

    public FlowExecutionSummary execute(Flow flow) {

        if (flow == null) throw new NoDefinedFlowException();

        if (flow.getSteps() == null || flow.getSteps().isEmpty())
            throw new NoDefinedStepsException();

        List<FlowExecutionSummaryDetail> resumeDetails = executeTasks(flow.getSteps());

        boolean successfulExecution = resumeDetails.stream()
                .allMatch(FlowExecutionSummaryDetail::successful);
        return new FlowExecutionSummary(flow.getName(), successfulExecution, resumeDetails);
    }

    private ServiceCall normalizeServiceCall(ServiceCall call, boolean hasExpectedValues) {

        if (!hasExpectedValues) return call;

        return new ServiceCall(
                PlaceholderResolver.resolve(context.variables(), call.url()),
                call.method(),
                call.headers(),
                PlaceholderResolver.resolve(context.variables(), call.body().toString())
        );
    }

    private List<FlowExecutionSummaryDetail> executeTasks(List<FlowStep> flowSteps) {
        return flowSteps.stream()
                .map(this::executeStep)
                .toList();
    }

    private FlowExecutionSummaryDetail executeStep(FlowStep step) {
        boolean hasExpectedValues = hasRequiredValues(step);

        ServiceCall normalizedCall = normalizeServiceCall(step.getServiceCall(), hasExpectedValues);

        CallResult response = serviceCaller.call(normalizedCall);

        boolean successfulExecution = response.statusCode() == HttpStatusCode.OK;

        exportVariables(response.responseBody(), step.getExport());

        return new FlowExecutionSummaryDetail(
                step.getStepName(),
                successfulExecution,
                normalizedCall,
                Duration.ZERO,
                response.responseBody()
        );
    }

    private boolean hasRequiredValues(FlowStep step) {
        return step.getRequires() != null && !step.getRequires().isEmpty();
    }

    private void exportVariables(String response, Map<String, String> variablesToExport) {
        if (variablesToExport != null && !variablesToExport.isEmpty()) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                JsonNode root = mapper.readTree(response);
                variablesToExport.forEach((key, keyValue) -> {
                    String valuePath = "/" + keyValue.replace(".", "/");
                    String value = root.at(valuePath).asText();
                    context.putVariable(key, value);
                });
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
