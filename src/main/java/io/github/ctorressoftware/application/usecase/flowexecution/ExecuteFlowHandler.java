package io.github.ctorressoftware.application.usecase.flowexecution;

import io.github.ctorressoftware.application.port.in.flowexecution.ExecuteFlowCommand;
import io.github.ctorressoftware.application.port.in.flowexecution.ExecuteFlowResult;
import io.github.ctorressoftware.application.port.in.flowexecution.ExecuteFlowUseCase;
import io.github.ctorressoftware.domain.model.ExecutionResume;

public class ExecuteFlowHandler implements ExecuteFlowUseCase {

    private final FlowExecutor executor;

    public ExecuteFlowHandler(FlowExecutor executor) {
        this.executor = executor;
    }

    @Override
    public ExecuteFlowResult execute(ExecuteFlowCommand command) {

        ExecutionResume resume = executor.execute(command.flow());

        return new ExecuteFlowResult(resume);
    }
}
