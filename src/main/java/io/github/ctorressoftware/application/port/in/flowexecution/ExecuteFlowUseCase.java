package io.github.ctorressoftware.application.port.in.flowexecution;

public interface ExecuteFlowUseCase {
    ExecuteFlowResult execute(ExecuteFlowCommand command);
}
