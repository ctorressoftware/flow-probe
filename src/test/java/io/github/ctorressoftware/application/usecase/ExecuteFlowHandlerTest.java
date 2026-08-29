package io.github.ctorressoftware.application.usecase;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.ctorressoftware.application.port.in.flowexecution.ExecuteFlowCommand;
import io.github.ctorressoftware.application.port.in.flowexecution.ExecuteFlowResult;
import io.github.ctorressoftware.application.port.out.Executor;
import io.github.ctorressoftware.domain.constant.HttpMethod;
import io.github.ctorressoftware.domain.model.Flow;
import io.github.ctorressoftware.domain.model.FlowExecutionSummary;
import io.github.ctorressoftware.domain.model.FlowExecutionSummaryDetail;
import io.github.ctorressoftware.domain.model.FlowStep;
import io.github.ctorressoftware.domain.model.ServiceCall;

@ExtendWith(MockitoExtension.class)
class ExecuteFlowHandlerTest {
    
    @Mock
    private Executor executor;

    private ExecuteFlowHandler executeFlowHandler;

    @BeforeEach
    void init() {
        this.executeFlowHandler = new ExecuteFlowHandler(executor);
    }

    @Test
    void shouldExecuteFlowAndReturnResult() {

        ServiceCall serviceCall = new ServiceCall(
                "https://pokeapi.co/api/v2/pokemon?offset=0&limit=1350",
                HttpMethod.GET,
                Map.of("accept", "application/json"),
                null
        );

        List<FlowStep> steps = List.of(
                FlowStep.create("flow", "first", serviceCall, null, null),
                FlowStep.create("flow", "second", serviceCall, null, null),
                FlowStep.create("flow", "third", serviceCall, null, null)
        );

        ExecuteFlowResult expected = new ExecuteFlowResult(
            new FlowExecutionSummary(
                "flow", 
                true, 
                List.of(
                    new FlowExecutionSummaryDetail("first", true, serviceCall, Duration.ZERO, ""),
                    new FlowExecutionSummaryDetail("second", true, serviceCall, Duration.ZERO, ""),
                    new FlowExecutionSummaryDetail("third", true, serviceCall, Duration.ZERO, "")
                )
            )
        );

        ExecuteFlowCommand command = new ExecuteFlowCommand(Flow.create("flow", steps));

        Mockito
            .when(executor.execute(Mockito.any(Flow.class)))
            .thenReturn(expected.resume());

        ExecuteFlowResult result = executeFlowHandler.execute(command);

        Assertions.assertEquals(expected, result);
        Mockito.verify(executor).execute(command.flow());
    }
}
