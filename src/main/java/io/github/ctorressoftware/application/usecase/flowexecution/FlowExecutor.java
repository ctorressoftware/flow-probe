package io.github.ctorressoftware.application.usecase.flowexecution;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ctorressoftware.application.port.out.ServiceCaller;
import io.github.ctorressoftware.domain.exception.NoDefinedFlowException;
import io.github.ctorressoftware.domain.exception.NoDefinedStepsException;
import io.github.ctorressoftware.domain.model.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class FlowExecutor {
    private final Context context;
    private final ServiceCaller serviceCaller;

    public FlowExecutor(Context context, ServiceCaller serviceCaller) {
        this.context = context;
        this.serviceCaller = serviceCaller;
    }

    public FlowExecutionSummary execute(Flow flow) {

        if (flow == null)
            throw new NoDefinedFlowException();

        if (flow.getSteps() == null || flow.getSteps().isEmpty())
            throw new NoDefinedStepsException();

        List<FlowExecutionSummaryDetail> resumeDetails = new ArrayList<>();

        flow.getSteps().forEach(step -> {
            
            ServiceCall call = step.getServiceCall();

            boolean callRequireValues = !step.getExpect().isEmpty();

            ServiceCall normalizeCall = resolvePlaceholders(call, callRequireValues);

            CallResult response = serviceCaller.call(normalizeCall);

            boolean successfulExecution = response != null && response.statusCode() == 200;
        
            resumeDetails.add(new FlowExecutionSummaryDetail(
                step.getStepName(),
                successfulExecution,
                call,
                Duration.ZERO, // TODO: Refactor this after adding startedAt and finishedAt fields
                response.responseBody()
            ));

            if (step.getExport() != null) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    JsonNode root = mapper.readTree(response.responseBody());
                    step.getExport().forEach((key, keyValue) -> {
                        String valuePath = "/" + keyValue.replace(".", "/");
                        String value = root.at(valuePath).asText();
                        context.putVariable(key, value);
                    });

                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        boolean successfulExecution = resumeDetails.stream().allMatch(FlowExecutionSummaryDetail::successful);
        return new FlowExecutionSummary(flow.getName(), successfulExecution, resumeDetails);
    }

    private ServiceCall resolvePlaceholders(ServiceCall call, boolean requireValues) {

        if (!requireValues) return call;

        return new ServiceCall(
                PlaceholderResolver.resolve(context.variables(), call.url()),
                call.method(),
                call.headers(),
                PlaceholderResolver.resolve(context.variables(), call.body().toString())
        );
    }

    private void exportVariables() {

    }
}
