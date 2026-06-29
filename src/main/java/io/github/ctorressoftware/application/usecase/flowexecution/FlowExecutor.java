package io.github.ctorressoftware.application.usecase.flowexecution;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ctorressoftware.application.port.out.ServiceCaller;
import io.github.ctorressoftware.domain.exception.NoDefinedFlowException;
import io.github.ctorressoftware.domain.exception.NoDefinedStepsException;
import io.github.ctorressoftware.domain.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FlowExecutor {
    private final Context context;
    private final ServiceCaller serviceCaller;

    public FlowExecutor(Context context, ServiceCaller serviceCaller) {
        this.context = context;
        this.serviceCaller = serviceCaller;
    }

    public ExecutionResume execute(Flow flow) {

        if (flow == null)
            throw new NoDefinedFlowException();

        if (flow.getSteps() == null || flow.getSteps().isEmpty())
            throw new NoDefinedStepsException();

        List<ExecutionResumeDetail> resumeDetails = new ArrayList<>();

        flow.getSteps().forEach(step -> {
            ExecutionResumeDetail detail = new ExecutionResumeDetail();
            detail.setStepName(step.getStepName());
            ServiceCall call = step.getServiceCall();

            call = step.getExpect() == null ? call : resolvePlaceholders(call);

            CallResult response = serviceCaller
                    .call(new ServiceCall(call.url(), call.method(), call.headers(), call.body()));

            String jsonResponse = Objects.isNull(response) ? null : response.responseBody();
            detail.setResponseString(jsonResponse);
            detail.setSuccessful(response != null && response.status() == 200);

            resumeDetails.add(detail);

            if (step.getExport() != null) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    JsonNode root = mapper.readTree(jsonResponse);
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

        boolean successfulExecution = resumeDetails.stream().allMatch(ExecutionResumeDetail::isSuccessful);
        return new ExecutionResume(flow.getName(), successfulExecution, resumeDetails);
    }

    private ServiceCall resolvePlaceholders(ServiceCall request) {

        return new ServiceCall(
                PlaceholderResolver.resolve(context.variables(), request.url()),
                request.method(),
                request.headers(),
                PlaceholderResolver.resolve(context.variables(), request.body().toString())
        );
    }
}
