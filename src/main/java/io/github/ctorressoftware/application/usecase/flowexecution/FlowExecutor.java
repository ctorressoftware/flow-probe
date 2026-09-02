package io.github.ctorressoftware.application.usecase.flowexecution;

import io.github.ctorressoftware.application.port.out.Executor;
import io.github.ctorressoftware.application.port.out.ServiceCaller;
import io.github.ctorressoftware.application.usecase.flowexecution.validation.ResponseValidator;
import io.github.ctorressoftware.application.usecase.flowexecution.validation.result.ResponseValidationResult;
import io.github.ctorressoftware.domain.exception.NoDefinedFlowException;
import io.github.ctorressoftware.domain.model.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FlowExecutor implements Executor {
    private final ContextManager contextManager;
    private final ServiceCaller serviceCaller;
    private final PlaceholderResolver placeholderResolver;
    private final ResponseValidator responseValidator;

    public FlowExecutor(
            ContextManager contextManager,
            ServiceCaller serviceCaller,
            PlaceholderResolver placeholderResolver,
            ResponseValidator responseValidator) {
        this.contextManager = Objects.requireNonNull(contextManager);
        this.serviceCaller = Objects.requireNonNull(serviceCaller);
        this.placeholderResolver = Objects.requireNonNull(placeholderResolver);
        this.responseValidator = responseValidator;
    }

    public FlowExecutionSummary execute(Flow flow) {

        if (flow == null) throw new NoDefinedFlowException();

        List<FlowExecutionSummaryDetail> resumeDetails = executeTasks(flow.steps());

        boolean successfulExecution = resumeDetails.stream()
                .allMatch(FlowExecutionSummaryDetail::successful);
        return new FlowExecutionSummary(flow.name(), successfulExecution, resumeDetails);
    }

    private List<FlowExecutionSummaryDetail> executeTasks(List<FlowStep> flowSteps) {

        List<FlowExecutionSummaryDetail> results = new ArrayList<>();

        for (FlowStep step : flowSteps) {
            FlowExecutionSummaryDetail result = executeStep(step);
            results.add(result);
            if (!result.successful()) break;
        }

        return List.copyOf(results);
    }

    private FlowExecutionSummaryDetail executeStep(FlowStep step) {

        ServiceCall normalizedCall = placeholderResolver
                .resolve(contextManager.getVariables(), step.serviceCall());

        CallResult response = serviceCaller.call(normalizedCall);

        ResponseValidationResult validationResult =
                responseValidator.validate(response, step.expectedResponse());

        if (!validationResult.successful()) {
            return FlowExecutionSummaryDetail.failure(
                    step.stepName(),
                    normalizedCall,
                    Duration.ZERO,
                    response.responseBody()
            );
        }

        contextManager.exportVariables(response.responseBody(), step.exports());

        return FlowExecutionSummaryDetail.success(
                step.stepName(),
                normalizedCall,
                Duration.ZERO,
                response.responseBody()
        );
    }
}
