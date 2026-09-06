package io.github.ctorressoftware;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import io.github.ctorressoftware.application.port.in.flowexecution.ExecuteFlowCommand;
import io.github.ctorressoftware.application.port.in.flowexecution.ExecuteFlowResult;
import io.github.ctorressoftware.application.port.in.flowexecution.ExecuteFlowUseCase;
import io.github.ctorressoftware.application.port.in.readfile.ReadFileCommand;
import io.github.ctorressoftware.application.port.in.readfile.ReadFileResult;
import io.github.ctorressoftware.application.port.in.readfile.ReadFileUseCase;
import io.github.ctorressoftware.application.port.out.Executor;
import io.github.ctorressoftware.application.port.out.JsonProcessor;
import io.github.ctorressoftware.application.port.out.ServiceCaller;
import io.github.ctorressoftware.application.usecase.ExecuteFlowHandler;
import io.github.ctorressoftware.application.usecase.ReadFileHandler;
import io.github.ctorressoftware.application.usecase.flowexecution.ContextManager;
import io.github.ctorressoftware.application.usecase.flowexecution.FlowExecutor;
import io.github.ctorressoftware.application.usecase.flowexecution.PlaceholderResolver;
import io.github.ctorressoftware.application.usecase.flowexecution.validation.BodyValidator;
import io.github.ctorressoftware.application.usecase.flowexecution.validation.DefaultResponseValidator;
import io.github.ctorressoftware.application.usecase.flowexecution.validation.ResponseValidator;
import io.github.ctorressoftware.application.usecase.flowexecution.validation.StatusValidator;
import io.github.ctorressoftware.application.usecase.flowexecution.validation.evaluator.EqualsExpectationEvaluator;
import io.github.ctorressoftware.application.usecase.flowexecution.validation.evaluator.ExpectationEvaluatorRegistry;
import io.github.ctorressoftware.application.usecase.flowexecution.validation.evaluator.NotEqualsExpectationEvaluator;
import io.github.ctorressoftware.domain.model.Context;
import io.github.ctorressoftware.domain.model.FilePath;
import io.github.ctorressoftware.domain.model.FlowExecutionSummary;
import io.github.ctorressoftware.infrastructure.callservice.RequestMapper;
import io.github.ctorressoftware.infrastructure.callservice.RestServiceCaller;
import io.github.ctorressoftware.infrastructure.json.jackson.JacksonJsonProcessor;
import io.github.ctorressoftware.infrastructure.readfile.yaml.YamlReader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

class FlowProbeE2ETest {

    @TempDir
    Path tempDir;

    private HttpServer server;

    private ObjectMapper objectMapper;
    private ReadFileUseCase readFileUseCase;
    private ExecuteFlowUseCase executeFlowUseCase;

    @BeforeEach
    void setUp() throws IOException {

        objectMapper = new ObjectMapper();

        JsonProcessor jsonProcessor =
                new JacksonJsonProcessor(objectMapper);

        ExpectationEvaluatorRegistry registry =
                new ExpectationEvaluatorRegistry(
                        List.of(
                                new EqualsExpectationEvaluator(),
                                new NotEqualsExpectationEvaluator()
                        )
                );

        ResponseValidator responseValidator =
                new DefaultResponseValidator(
                        new StatusValidator(registry),
                        new BodyValidator(jsonProcessor, registry)
                );

        RequestMapper requestMapper =
                new RequestMapper(
                        jsonProcessor,
                        Duration.ofSeconds(5)
                );

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        ServiceCaller serviceCaller =
                new RestServiceCaller(
                        httpClient,
                        requestMapper
                );

        ContextManager contextManager =
                new ContextManager(
                        new Context(),
                        jsonProcessor
                );

        Executor executor =
                new FlowExecutor(
                        contextManager,
                        serviceCaller,
                        new PlaceholderResolver(),
                        responseValidator
                );

        readFileUseCase =
                new ReadFileHandler(
                        new YamlReader()
                );

        executeFlowUseCase =
                new ExecuteFlowHandler(executor);

        server = HttpServer.create(
                new InetSocketAddress(0),
                0
        );
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void shouldExecuteCompleteFlowPreservingExportedTypes()
            throws Exception {

        AtomicReference<String> receivedMethod =
                new AtomicReference<>();

        AtomicReference<String> receivedHeader =
                new AtomicReference<>();

        AtomicReference<String> receivedBody =
                new AtomicReference<>();

        server.createContext(
                "/session",
                exchange -> respond(
                        exchange,
                        200,
                        """
                        {
                          "id": 25,
                          "enabled": true
                        }
                        """
                )
        );

        server.createContext(
                "/session/25",
                exchange -> {

                    receivedMethod.set(
                            exchange.getRequestMethod()
                    );

                    receivedHeader.set(
                            exchange.getRequestHeaders()
                                    .getFirst("X-Enabled")
                    );

                    receivedBody.set(
                            new String(
                                    exchange.getRequestBody()
                                            .readAllBytes(),
                                    StandardCharsets.UTF_8
                            )
                    );

                    respond(
                            exchange,
                            200,
                            """
                            {
                              "accepted": true,
                              "id": 25
                            }
                            """
                    );
                }
        );

        server.start();

        int port = server.getAddress().getPort();

        String yaml = """
                name: "e2e-success"

                steps:
                  - name: "create-session"
                    request:
                      url: "http://localhost:%d/session"
                      method: "GET"

                    expect:
                      status: 200
                      body:
                        - path: "/id"
                          operator: "equals"
                          value: 25
                        - path: "/enabled"
                          operator: "equals"
                          value: true

                    exports:
                      sessionId: "/id"
                      enabled: "/enabled"

                  - name: "use-session"
                    request:
                      url: "http://localhost:%d/session/${sessionId}"
                      method: "POST"
                      headers:
                        Content-Type: "application/json"
                        X-Enabled: "${enabled}"
                      body:
                        id: "${sessionId}"
                        enabled: "${enabled}"
                        message: "session-${sessionId}"

                    expect:
                      status: 200
                      body:
                        - path: "/accepted"
                          operator: "equals"
                          value: true
                        - path: "/id"
                          operator: "equals"
                          value: 25
                """.formatted(port, port);

        FlowExecutionSummary summary = execute(yaml);

        Assertions.assertTrue(
                summary.successfulExecution()
        );

        Assertions.assertEquals(
                2,
                summary.stepsResults().size()
        );

        Assertions.assertEquals(
                "POST",
                receivedMethod.get()
        );

        Assertions.assertEquals(
                "true",
                receivedHeader.get()
        );

        JsonNode body =
                objectMapper.readTree(
                        receivedBody.get()
                );

        Assertions.assertTrue(
                body.get("id").isInt()
        );

        Assertions.assertEquals(
                25,
                body.get("id").intValue()
        );

        Assertions.assertTrue(
                body.get("enabled").isBoolean()
        );

        Assertions.assertTrue(
                body.get("enabled").booleanValue()
        );

        Assertions.assertEquals(
                "session-25",
                body.get("message").textValue()
        );
    }

    @Test
    void shouldStopFlowAfterFailedStep()
            throws Exception {

        AtomicInteger secondEndpointCalls =
                new AtomicInteger();

        server.createContext(
                "/failure",
                exchange ->
                        respond(
                                exchange,
                                500,
                                """
                                {
                                  "error": "boom"
                                }
                                """
                        )
        );

        server.createContext(
                "/should-not-run",
                exchange -> {

                    secondEndpointCalls.incrementAndGet();

                    respond(
                            exchange,
                            200,
                            """
                            {
                              "executed": true
                            }
                            """
                    );
                }
        );

        server.start();

        int port = server.getAddress().getPort();

        String yaml = """
                name: "e2e-failure"

                steps:
                  - name: "failing-step"
                    request:
                      url: "http://localhost:%d/failure"
                      method: "GET"

                    expect:
                      status: 200

                  - name: "must-not-run"
                    request:
                      url: "http://localhost:%d/should-not-run"
                      method: "GET"

                    expect:
                      status: 200
                """.formatted(port, port);

        FlowExecutionSummary summary = execute(yaml);

        Assertions.assertFalse(
                summary.successfulExecution()
        );

        Assertions.assertEquals(
                1,
                summary.stepsResults().size()
        );

        Assertions.assertFalse(
                summary.stepsResults()
                        .getFirst()
                        .successful()
        );

        Assertions.assertEquals(
                0,
                secondEndpointCalls.get()
        );
    }

    private FlowExecutionSummary execute(String yaml)
            throws IOException {

        Path file = tempDir.resolve("flow.yaml");

        Files.writeString(
                file,
                yaml,
                StandardCharsets.UTF_8
        );

        ReadFileResult readResult =
                readFileUseCase.read(
                        new ReadFileCommand(
                                new FilePath(
                                        file.toString()
                                )
                        )
                );

        ExecuteFlowResult executeResult =
                executeFlowUseCase.execute(
                        new ExecuteFlowCommand(
                                readResult.flow()
                        )
                );

        return executeResult.resume();
    }

    private void respond(
            HttpExchange exchange,
            int status,
            String body
    ) throws IOException {

        byte[] bytes =
                body.getBytes(
                        StandardCharsets.UTF_8
                );

        exchange.getResponseHeaders()
                .set(
                        "Content-Type",
                        "application/json"
                );

        exchange.sendResponseHeaders(
                status,
                bytes.length
        );

        try (var output =
                     exchange.getResponseBody()) {

            output.write(bytes);
        }
    }
}