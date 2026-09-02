package io.github.ctorressoftware.infrastructure.cli;

import io.github.ctorressoftware.application.port.in.createticket.CreateImpedimentTicketCommand;
import io.github.ctorressoftware.application.port.in.createticket.CreateImpedimentTicketResult;
import io.github.ctorressoftware.application.port.in.createticket.CreateImpedimentTicketUseCase;
import io.github.ctorressoftware.application.port.in.flowexecution.ExecuteFlowCommand;
import io.github.ctorressoftware.application.port.in.flowexecution.ExecuteFlowResult;
import io.github.ctorressoftware.application.port.in.flowexecution.ExecuteFlowUseCase;
import io.github.ctorressoftware.application.port.in.readfile.ReadFileCommand;
import io.github.ctorressoftware.application.port.in.readfile.ReadFileResult;
import io.github.ctorressoftware.application.port.in.readfile.ReadFileUseCase;
import io.github.ctorressoftware.application.port.out.RequestRenderer;
import io.github.ctorressoftware.domain.constant.HttpMethod;
import io.github.ctorressoftware.domain.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import picocli.CommandLine;

import java.io.PrintStream;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@ExtendWith(MockitoExtension.class)
class RunCommandTest {

    @Mock
    private PrintStream out;

    @Mock
    private Scanner scanner;

    @Mock
    private RequestRenderer requestRenderer;

    @Mock
    private ReadFileUseCase readFileUseCase;

    @Mock
    private ExecuteFlowUseCase executeFlowUseCase;

    @Mock
    private CreateImpedimentTicketUseCase createImpedimentTicketUseCase;

    private RunCommand runCommand;

    @BeforeEach
    void init() {
        this.runCommand = new RunCommand(
                out,
                scanner,
                requestRenderer,
                readFileUseCase,
                executeFlowUseCase,
                createImpedimentTicketUseCase
        );
    }

    @Test
    void shouldExecuteRunCommandSuccessfully() {

        ServiceCall getAll = new ServiceCall(
                "https://pokeapi.co/api/v2/pokemon?offset=0&limit=1350",
                HttpMethod.GET,
                Map.of("accept", "application/json"),
                null
        );

        ServiceCall getPikachu = new ServiceCall(
                "https://pokeapi.co/api/v2/pokemon/${pokemonName}",
                HttpMethod.GET,
                Map.of("accept", "application/json"),
                null
        );

        List<FlowStep> steps = List.of(
                FlowStep.create(
                        "flow",
                        "first",
                        getAll,
                        null,
                        Map.of("pokemonName", "/results/0/name")
                ),
                FlowStep.create(
                        "flow",
                        "second",
                        getPikachu,
                        null,
                        null
                )
        );

        Flow flow = Flow.create("flow", steps);

        FlowExecutionSummary resume = new FlowExecutionSummary(
                "flow",
                true,
                List.of(
                        new FlowExecutionSummaryDetail(
                                "first",
                                true,
                                getAll,
                                Duration.ZERO,
                                "{}"
                        ),
                        new FlowExecutionSummaryDetail(
                                "second",
                                true,
                                getPikachu,
                                Duration.ZERO,
                                "{}"
                        )
                )
        );

        Mockito
                .when(readFileUseCase.read(Mockito.any(ReadFileCommand.class)))
                .thenReturn(new ReadFileResult(flow));

        Mockito
                .when(executeFlowUseCase.execute(new ExecuteFlowCommand(flow)))
                .thenReturn(new ExecuteFlowResult(resume));

        FlowProbeCommand rootCommand = new FlowProbeCommand();
        CommandLine cmd = new CommandLine(rootCommand);
        cmd.addSubcommand("run", runCommand);

        int exitCode = cmd.execute(
                "run",
                "--file",
                "src/test/resources/flow-failure.yaml"
        );

        Assertions.assertEquals(ExitCode.SUCCESS.code(), exitCode);

        ArgumentCaptor<ReadFileCommand> readCaptor =
                ArgumentCaptor.forClass(ReadFileCommand.class);

        Mockito.verify(readFileUseCase)
                .read(readCaptor.capture());

        Assertions.assertEquals(
                "src/test/resources/flow-failure.yaml",
                readCaptor.getValue().filePath().value()
        );

        ArgumentCaptor<ExecuteFlowCommand> executeCaptor =
                ArgumentCaptor.forClass(ExecuteFlowCommand.class);

        Mockito.verify(executeFlowUseCase)
                .execute(executeCaptor.capture());

        Assertions.assertEquals(
                flow,
                executeCaptor.getValue().flow()
        );

        Mockito.verifyNoMoreInteractions(
                readFileUseCase,
                executeFlowUseCase
        );
    }

    @Test
    void shouldFailWhenFilePathArgumentIsNotProvided() {

        FlowProbeCommand rootCommand = new FlowProbeCommand();
        CommandLine commandLine = new CommandLine(rootCommand);
        commandLine
                .setParameterExceptionHandler((e, args) -> ExitCode.INVALID_ARGUMENTS.code())
                .addSubcommand("run", runCommand);

        int exitCode = commandLine.execute("run");

        Assertions.assertEquals(
                ExitCode.INVALID_ARGUMENTS.code(),
                exitCode
        );

        Mockito.verifyNoInteractions(
                readFileUseCase,
                executeFlowUseCase
        );
    }

    @Test
    void shouldFailWhenFilePathIsBlank() {

        FlowProbeCommand rootCommand = new FlowProbeCommand();
        CommandLine cmd = new CommandLine(rootCommand);
        cmd.addSubcommand("run", runCommand);

        int exitCode = cmd.execute("run", "--file", " ");

        Assertions.assertEquals(ExitCode.EXECUTION_ERROR.code(), exitCode);

        Mockito.verifyNoInteractions(
                readFileUseCase,
                executeFlowUseCase
        );
    }

    @Test
    void shouldFailWhenExecuteFlowWithUnknownException() {

        ServiceCall getAll = new ServiceCall(
                "https://pokeapi.co/api/v2/pokemon?offset=0&limit=1350",
                HttpMethod.GET,
                Map.of("accept", "application/json"),
                null
        );

        ServiceCall getPikachu = new ServiceCall(
                "https://pokeapi.co/api/v2/pokemon/${pokemonName}",
                HttpMethod.GET,
                Map.of("accept", "application/json"),
                null
        );

        List<FlowStep> steps = List.of(
                FlowStep.create(
                        "flow",
                        "first",
                        getAll,
                        null,
                        Map.of("pokemonName", "/results/0/name")
                ),
                FlowStep.create(
                        "flow",
                        "second",
                        getPikachu,
                        null,
                        null
                )
        );

        Flow flow = Flow.create("flow", steps);

        Mockito
                .when(readFileUseCase.read(Mockito.any(ReadFileCommand.class)))
                .thenReturn(new ReadFileResult(flow));

        Mockito
                .when(executeFlowUseCase.execute(new ExecuteFlowCommand(flow)))
                .thenThrow(new RuntimeException());

        FlowProbeCommand rootCommand = new FlowProbeCommand();
        CommandLine cmd = new CommandLine(rootCommand);
        cmd.addSubcommand("run", runCommand);

        int exitCode = cmd.execute(
                "run",
                "--file",
                "src/test/resources/flow-failure.yaml"
        );

        Assertions.assertEquals(ExitCode.EXECUTION_ERROR.code(), exitCode);

        ArgumentCaptor<ReadFileCommand> readCaptor =
                ArgumentCaptor.forClass(ReadFileCommand.class);

        Mockito.verify(readFileUseCase)
                .read(readCaptor.capture());

        Assertions.assertEquals(
                "src/test/resources/flow-failure.yaml",
                readCaptor.getValue().filePath().value()
        );

        ArgumentCaptor<ExecuteFlowCommand> executeCaptor =
                ArgumentCaptor.forClass(ExecuteFlowCommand.class);

        Mockito.verify(executeFlowUseCase)
                .execute(executeCaptor.capture());

        Assertions.assertEquals(
                flow,
                executeCaptor.getValue().flow()
        );

        Mockito.verifyNoMoreInteractions(
                readFileUseCase,
                executeFlowUseCase
        );
    }

    @Test
    void shouldExecuteRunCommandAndCreateImpedimentTicket() {

        ServiceCall getAll = new ServiceCall(
                "https://pokeapi.co/api/v2/pokemon?offset=0&limit=1350",
                HttpMethod.GET,
                Map.of("accept", "application/json"),
                null
        );

        ServiceCall getPikachu = new ServiceCall(
                "https://pokeapi.co/api/v2/pokemon/${pokemonName}",
                HttpMethod.GET,
                Map.of("accept", "application/json"),
                null
        );

        List<FlowStep> steps = List.of(
                FlowStep.create(
                        "flow",
                        "first",
                        getAll,
                        null,
                        Map.of("pokemonName", "/results/0/name")
                ),
                FlowStep.create(
                        "flow",
                        "second",
                        getPikachu,
                        null,
                        null
                )
        );

        Flow flow = Flow.create("flow", steps);

        FlowExecutionSummary resume = new FlowExecutionSummary(
                "flow",
                false,
                List.of(
                        new FlowExecutionSummaryDetail(
                                "first",
                                true,
                                getAll,
                                Duration.ZERO,
                                "{}"
                        ),
                        new FlowExecutionSummaryDetail(
                                "second",
                                false,
                                getPikachu,
                                Duration.ZERO,
                                "{}"
                        )
                )
        );

        ImpedimentTicket ticket = ImpedimentTicket.restore(1L, "Title", "Description");

        Mockito
                .when(readFileUseCase.read(Mockito.any(ReadFileCommand.class)))
                .thenReturn(new ReadFileResult(flow));

        Mockito
                .when(executeFlowUseCase.execute(new ExecuteFlowCommand(flow)))
                .thenReturn(new ExecuteFlowResult(resume));

        Mockito
                .when(scanner.next())
                .thenReturn("Y");

        Mockito
                .when(createImpedimentTicketUseCase.createTicket(Mockito.any(CreateImpedimentTicketCommand.class)))
                .thenReturn(new CreateImpedimentTicketResult(ticket));

        FlowProbeCommand rootCommand = new FlowProbeCommand();
        CommandLine cmd = new CommandLine(rootCommand);
        cmd.addSubcommand("run", runCommand);

        int exitCode = cmd.execute(
                "run",
                "--file",
                "src/test/resources/flow-failure.yaml"
        );

        Assertions.assertEquals(ExitCode.SUCCESS.code(), exitCode);

        ArgumentCaptor<ReadFileCommand> readCaptor =
                ArgumentCaptor.forClass(ReadFileCommand.class);

        Mockito.verify(readFileUseCase)
                .read(readCaptor.capture());

        Assertions.assertEquals(
                "src/test/resources/flow-failure.yaml",
                readCaptor.getValue().filePath().value()
        );

        ArgumentCaptor<ExecuteFlowCommand> executeCaptor =
                ArgumentCaptor.forClass(ExecuteFlowCommand.class);

        Mockito.verify(executeFlowUseCase)
                .execute(executeCaptor.capture());

        Assertions.assertEquals(
                flow,
                executeCaptor.getValue().flow()
        );

        Mockito.verifyNoMoreInteractions(
                readFileUseCase,
                executeFlowUseCase
        );
    }
}
