package io.github.ctorressoftware.application.usecase.flowexecution;

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
    private ServiceCaller serviceCaller;

    private ContextManager contextManager;

    private JsonProcessor jsonProcessor;

    private FlowExecutor flowExecutor;

    @BeforeEach
    void init() {
        this.jsonProcessor = new JacksonJsonProcessor(new ObjectMapper());
        this.contextManager = new ContextManager(new Context(), jsonProcessor);
        this.flowExecutor = new FlowExecutor(
                contextManager,
                serviceCaller,
                new PlaceholderResolver(jsonProcessor)
        );
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
    void shouldExecuteFlowWithMultipleStepsExportingVariables() {

        String getAllResponse = "{\"results\":[{\"name\":\"Pikachu\"}]}";

        String getPikachuResponse = "{\"name\":\"Pikachu\"}";

        ServiceCall getAll = new ServiceCall(
                "https://pokeapi.co/api/v2/pokemon?offset=0&limit=1350",
                HttpMethod.GET,
                Map.of("accept", "application/json"),
                null
        );

        ServiceCall getPikachuUnresolved = new ServiceCall(
                "https://pokeapi.co/api/v2/pokemon/${pokemonName}",
                HttpMethod.GET,
                Map.of("accept", "application/json"),
                null
        );

        ServiceCall getPikachuResolved = new ServiceCall(
                "https://pokeapi.co/api/v2/pokemon/Pikachu",
                HttpMethod.GET,
                Map.of("accept", "application/json"),
                null
        );

        Flow flow = Flow.create("Flow", List.of(
                FlowStep.create(
                        "Flow",
                        "get-all-pokemon",
                        getAll,
                        null,
                        Map.of("pokemonName", "results.0.name")
                ),
                FlowStep.create(
                        "Flow",
                        "get-pikachu",
                        getPikachuUnresolved,
                        Map.of("name", "${pokemonName}"),
                        null
                )
        ));

        Mockito
                .when(serviceCaller.call(getAll))
                .thenReturn(new CallResult(HttpStatusCode.OK, getAllResponse));

        Mockito
                .when(serviceCaller.call(getPikachuResolved))
                .thenReturn(new CallResult(HttpStatusCode.OK, getPikachuResponse));

        FlowExecutionSummary summary = flowExecutor.execute(flow);

        Assertions.assertNotNull(summary);
        Assertions.assertEquals(summary.getFlowName(), flow.getSteps().getFirst().getFlowName());
        Assertions.assertTrue(summary.isSuccessfulExecution());
        Assertions.assertEquals(2, summary.getStepsResults().size());
        Mockito.verify(serviceCaller, Mockito.times(1)).call(getAll);
        Mockito.verify(serviceCaller, Mockito.times(1)).call(getPikachuResolved);
        Mockito.verifyNoMoreInteractions(serviceCaller);
    }

    @Test
    void shouldThrowNoDefinedFlowException() {
        Assertions.assertThrows(
                NoDefinedFlowException.class,
                () -> flowExecutor.execute(null)
        );
    }
}
