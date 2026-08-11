package io.github.ctorressoftware.application.usecase.flowexecution;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ctorressoftware.application.port.out.JsonProcessor;
import io.github.ctorressoftware.application.port.out.ServiceCaller;
import io.github.ctorressoftware.domain.constant.HttpMethod;
import io.github.ctorressoftware.domain.constant.HttpStatusCode;
import io.github.ctorressoftware.domain.exception.NoDefinedFlowException;
import io.github.ctorressoftware.domain.model.*;
import io.github.ctorressoftware.infrastructure.json.jackson.JacksonJsonProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class FlowExecutorTest {

    @Mock
    private ContextManager contextManager;

    @Mock
    private ServiceCaller serviceCaller;

    private JsonProcessor jsonProcessor;

    private FlowExecutor flowExecutor;

    @BeforeEach
    void init() {
        this.jsonProcessor = new JacksonJsonProcessor(new ObjectMapper());
        PlaceholderResolver placeholderResolver = new PlaceholderResolver(jsonProcessor);
        flowExecutor = new FlowExecutor(contextManager, serviceCaller, placeholderResolver);
    }

    @Test
    void shouldExecuteFlowWithMultipleStepsWithoutExportingVariables() {

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

        CallResult result = new CallResult(HttpStatusCode.OK, null);

        Mockito
                .when(contextManager.getVariables())
                .thenReturn(List.of());

        Mockito
                .when(serviceCaller.call(Mockito.any(ServiceCall.class)))
                .thenReturn(result);

        FlowExecutionSummary summary =
                flowExecutor.execute(Flow.create("flow", steps));

        Assertions.assertNotNull(summary);

        Assertions.assertEquals(
                steps.getFirst().getFlowName(),
                summary.getFlowName()
        );

        Assertions.assertTrue(summary.isSuccessfulExecution());

        Assertions.assertEquals(
                3,
                summary.getStepsResults().size()
        );

        ArgumentCaptor<ServiceCall> captor =
                ArgumentCaptor.forClass(ServiceCall.class);

        Mockito
                .verify(serviceCaller, Mockito.times(3))
                .call(captor.capture());

        List<ServiceCall> actualCalls = captor.getAllValues();

        Assertions.assertEquals(3, actualCalls.size());

        actualCalls.forEach(actualCall -> {
            Assertions.assertEquals(serviceCall.url(), actualCall.url());
            Assertions.assertEquals(serviceCall.method(), actualCall.method());
            Assertions.assertEquals(serviceCall.headers(), actualCall.headers());
            Assertions.assertEquals(serviceCall.body(), actualCall.body());
            Assertions.assertEquals(serviceCall, actualCall);
        });
    }

    @Test
    void shouldExecuteFlowWithMultipleStepsExportingVariables() throws JsonProcessingException {

        ServiceCall getAll = new ServiceCall(
                "https://pokeapi.co/api/v2/pokemon?offset=0&limit=1350",
                HttpMethod.GET,
                Map.of("accept", "application/json"),
                null
        );

        String firstResponse = """
                {
                  "results": [
                    {
                      "name": "Pikachu"
                    }
                  ]
                }
                """;

        ServiceCall getPikachu = new ServiceCall(
                "https://pokeapi.co/api/v2/pokemon/${pokemonName}",
                HttpMethod.GET,
                Map.of("accept", "application/json"),
                null
        );

        String secondResponse = """
                {
                  "name": "Pikachu"
                }
                """;

        List<FlowStep> steps = List.of(
                FlowStep.create(
                        "flow",
                        "first",
                        getAll,
                        null,
                        Map.of("pokemonName", "results.0.name")
                ),
                FlowStep.create(
                        "flow",
                        "second",
                        getPikachu,
                        Map.of("name", "${pokemonName}"),
                        null
                )
        );

        CallResult firstResult = new CallResult(HttpStatusCode.OK, jsonProcessor.serialize(firstResponse));
        CallResult secondResult = new CallResult(HttpStatusCode.OK, jsonProcessor.serialize(secondResponse));

        Mockito.when(serviceCaller.call(getAll)).thenReturn(firstResult);
        Mockito.when(serviceCaller.call(getPikachu)).thenReturn(secondResult);
        FlowExecutionSummary summary = flowExecutor.execute(Flow.create("flow", steps));

        Assertions.assertNotNull(summary);
        Assertions.assertEquals(summary.getFlowName(), steps.getFirst().getFlowName());
        Assertions.assertTrue(summary.isSuccessfulExecution());
        Assertions.assertEquals(2, summary.getStepsResults().size());
        Mockito.verify(serviceCaller, Mockito.times(1)).call(getAll);
        Mockito.verify(serviceCaller, Mockito.times(1)).call(getPikachu);
    }

    @Test
    void shouldThrowNoDefinedFlowException() {
        Assertions.assertThrows(
                NoDefinedFlowException.class,
                () -> flowExecutor.execute(null)
        );
    }
}
