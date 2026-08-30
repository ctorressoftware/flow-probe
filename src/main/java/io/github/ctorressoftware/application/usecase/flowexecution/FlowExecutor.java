package io.github.ctorressoftware.application.usecase.flowexecution;

import io.github.ctorressoftware.application.port.out.Executor;
import io.github.ctorressoftware.application.port.out.ServiceCaller;
import io.github.ctorressoftware.domain.constant.HttpStatusCode;
import io.github.ctorressoftware.domain.exception.NoDefinedFlowException;
import io.github.ctorressoftware.domain.model.*;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class FlowExecutor implements Executor {
    private final ContextManager contextManager;
    private final ServiceCaller serviceCaller;
    private final PlaceholderResolver placeholderResolver;

    public FlowExecutor(
            ContextManager contextManager,
            ServiceCaller serviceCaller,
            PlaceholderResolver placeholderResolver) {
        this.contextManager = Objects.requireNonNull(contextManager);
        this.serviceCaller = Objects.requireNonNull(serviceCaller);
        this.placeholderResolver = Objects.requireNonNull(placeholderResolver);
    }

    public FlowExecutionSummary execute(Flow flow) {

        if (flow == null) throw new NoDefinedFlowException();

        List<FlowExecutionSummaryDetail> resumeDetails = executeTasks(flow.steps());

        boolean successfulExecution = resumeDetails.stream()
                .allMatch(FlowExecutionSummaryDetail::successful);
        return new FlowExecutionSummary(flow.name(), successfulExecution, resumeDetails);
    }

    private List<FlowExecutionSummaryDetail> executeTasks(List<FlowStep> flowSteps) {
        return flowSteps.stream()
                .map(this::executeStep)
                .toList();
    }

    private FlowExecutionSummaryDetail executeStep(FlowStep step) {

        ServiceCall normalizedCall = placeholderResolver
                .resolve(contextManager.getVariables(), step.serviceCall());

        CallResult response = serviceCaller.call(normalizedCall);

        boolean successfulExecution = HttpStatusCode.isSuccess(response.statusCode());

        contextManager.exportVariables(response.responseBody(), step.export());

        return new FlowExecutionSummaryDetail(
                step.stepName(),
                successfulExecution,
                normalizedCall,
                Duration.ZERO,
                response.responseBody()
        );
    }
}
