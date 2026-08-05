package io.github.ctorressoftware.application.usecase.flowexecution;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ctorressoftware.application.port.out.ServiceCaller;
import io.github.ctorressoftware.domain.constant.HttpMethod;
import io.github.ctorressoftware.domain.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class FlowExecutorTest {

    @Mock
    private Context context;

    @Mock
    private ServiceCaller serviceCaller;

    @Mock
    private ObjectMapper objectMapper;

    private FlowExecutor flowExecutor;

    @BeforeEach
    void init() {
        flowExecutor = new FlowExecutor(context, serviceCaller, objectMapper);
    }

    @Test
    void shouldExecuteFlowWithStepsCorrectly() {

        ServiceCall call = new ServiceCall(
                "https://pokeapi.co/api/v2/pokemon?offset=0&limit=1350",
                HttpMethod.GET,
                Map.of("accept", "application/json"),
                null
        );

        FlowStep step = FlowStep.create(
                "flow",
                "first_step",
                call,
                null,
                null
        );

        Mockito.when(serviceCaller.call(call)).thenReturn(
                new CallResult(200, null)
        );

        FlowExecutionSummary summary = flowExecutor
                .execute(Flow.create("flow", List.of(step)));

        Assertions.assertNotNull(summary);
        Assertions.assertEquals(summary.getFlowName(), step.getFlowName());
        Assertions.assertTrue(summary.isSuccessfulExecution());
        Assertions.assertEquals(1, summary.getStepsResults().size());
    }
}
