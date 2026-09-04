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
import io.github.ctorressoftware.domain.model.*;
import io.github.ctorressoftware.infrastructure.cli.exception.MissingFilepathException;
import picocli.CommandLine;

import java.io.PrintStream;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@CommandLine.Command(name = "run")
public class RunCommand implements Callable<Integer> {
    private final PrintStream out;
    private final RequestRenderer requestRenderer;
    private final ReadFileUseCase readFileUseCase;
    private final ExecuteFlowUseCase executeFlowUseCase;
    private final CreateImpedimentTicketUseCase createImpedimentTicketUseCase;

    @CommandLine.Option(
            names = {"--file"},
            paramLabel = "FILEPATH",
            description = "Required YAML file path to read it",
            required = true
    )
    private String filePath;

    @CommandLine.Option(
            names = {"--create-impediment"},
            description = "Create an impediment when the flow fails"
    )
    private Boolean impedimentCreation;

    public RunCommand(
            PrintStream out,
            RequestRenderer requestRenderer,
            ReadFileUseCase readFileUseCase,
            ExecuteFlowUseCase executeFlowUseCase,
            CreateImpedimentTicketUseCase createImpedimentTicketUseCase
    ) {
        this.out = out;
        this.requestRenderer = requestRenderer;
        this.readFileUseCase = readFileUseCase;
        this.executeFlowUseCase = executeFlowUseCase;
        this.createImpedimentTicketUseCase = createImpedimentTicketUseCase;
    }

    @Override
    public Integer call() {
        try {
            return run();
        } catch (Exception e) {
            String reason = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            out.println("FlowProbe failed to run. " + reason);
            return ExitCode.EXECUTION_ERROR.code();
        }
    }

    private Integer run() {
        if (filePath.isBlank()) throw new MissingFilepathException();
        ReadFileResult readFileResult = readFileUseCase.read(new ReadFileCommand(new FilePath(filePath)));
        Flow flow = readFileResult.flow();
        ExecuteFlowResult executeFlowResult = executeFlowUseCase.execute(new ExecuteFlowCommand(flow));
        FlowExecutionSummary resume = executeFlowResult.resume();
        renderReproducibleRequests(resume);

        if (!resume.successfulExecution() && impedimentCreation) {
            ImpedimentTicket ticket = createTicketFromResume(resume);
            CreateImpedimentTicketResult ticketCreationResult = createImpedimentTicketUseCase
                    .createTicket(new CreateImpedimentTicketCommand(ticket));
            ImpedimentTicket impedimentTicket = ticketCreationResult.created();
            out.println("Impediment ticket created. ID = " + impedimentTicket.id());
        }

        return resume.successfulExecution() ?
                ExitCode.SUCCESS.code() :
                ExitCode.EXECUTION_ERROR.code();
    }

    private void renderReproducibleRequests(FlowExecutionSummary resume) {
        resume.stepsResults().forEach(detail -> {
            ServiceCall call = detail.executed();
            ReproducibleRequest reproducibleRequest = ReproducibleRequest.fromServiceCall(call);
            String request = requestRenderer.render(reproducibleRequest);
            out.println(request);
        });
    }

    private ImpedimentTicket createTicketFromResume(FlowExecutionSummary resume) {

        String title = "Impediment ticket: " + resume.flowName();

        List<FlowExecutionSummaryDetail> failures = resume.stepsResults().stream()
                .filter(x -> !x.successful())
                .toList();

        String description = failures.stream().map(detail -> {
            ServiceCall call = detail.executed();
            ReproducibleRequest reproducibleRequest = ReproducibleRequest.fromServiceCall(call);
            return requestRenderer.render(reproducibleRequest);
        }).collect(Collectors.joining("\n\n"));

        return ImpedimentTicket.create(title, description);
    }
}
