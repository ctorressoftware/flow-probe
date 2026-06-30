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
import picocli.CommandLine;

import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public class RunCommand implements Callable<Integer> {

    private final Scanner scanner;
    private final RequestRenderer requestRenderer;

    @CommandLine.Option(
            names = {"-f", "--file"},
            required = true,
            paramLabel = "FILE",
            description = "Required YAML file path to read it"
    )
    private String filePath;

    @CommandLine.Option(
            names = {"-i", "--impediment", "--create-impediment"},
            required = false,
            paramLabel = "IMPEDIMENT",
            description = "Flag to know if create an impediment or not",
            fallbackValue = "false",
            interactive = true
    )
    private Boolean impedimentCreation;

    @CommandLine.Option(
            names = {"-r", "--request-format"},
            paramLabel = "REQUEST_FORMAT",
            description = "Flag to know the request format to render",
            fallbackValue = "false"
    )
    private final RequestFormat requestFormat = RequestFormat.CURL;

    private final ReadFileUseCase readFileUseCase;
    private final ExecuteFlowUseCase executeFlowUseCase;
    private final CreateImpedimentTicketUseCase createImpedimentTicketUseCase;

    public RunCommand(
            Scanner scanner,
            RequestRenderer requestRenderer,
            ReadFileUseCase readFileUseCase,
            ExecuteFlowUseCase executeFlowUseCase,
            CreateImpedimentTicketUseCase createImpedimentTicketUseCase
    ) {
        this.scanner = scanner;
        this.requestRenderer = requestRenderer;
        this.readFileUseCase = readFileUseCase;
        this.executeFlowUseCase = executeFlowUseCase;
        this.createImpedimentTicketUseCase = createImpedimentTicketUseCase;
    }

    @Override
    public Integer call() {
        ReadFileResult readFileResult = readFileUseCase.read(new ReadFileCommand(new FilePath(filePath)));
        Flow flow = readFileResult.flow();
        ExecuteFlowResult executeFlowResult = executeFlowUseCase.execute(new ExecuteFlowCommand(flow));
        FlowExecutionSummary resume = executeFlowResult.resume();
        // printFlowResume(resume); // TODO: adjust this to implement CurlGenerator for requests
        printReproducibleRequests(flow);

        if (!resume.isSuccessfulExecution() && Objects.isNull(impedimentCreation)) {
            System.out.print("Do you want to create an impediment? (Y/N): ");
            impedimentCreation = scanner.next().equalsIgnoreCase("Y");
        }

        if (!resume.isSuccessfulExecution() && impedimentCreation) {
            ImpedimentTicket ticket = createTicketFromResume(resume);
            CreateImpedimentTicketResult ticketCreationResult = createImpedimentTicketUseCase
                    .createTicket(new CreateImpedimentTicketCommand(ticket));
            ImpedimentTicket impedimentTicket = ticketCreationResult.created();
            System.out.println("Impediment ticket created. ID = " + impedimentTicket.getId());
        }

        return 0;
    }

    private void printReproducibleRequests(Flow flow) {
        flow.getSteps().forEach(s -> {
            ServiceCall call = s.getServiceCall();
            ReproducibleRequest request = new ReproducibleRequest(
                    call.url(),
                    call.method(),
                    call.headers(),
                    String.valueOf(call.body())
            );
            String reproducibleRequestString = requestRenderer.render(request);
            System.out.println(reproducibleRequestString);
        });
    }

    private void printFlowResume(FlowExecutionSummary resume) {
        System.out.println("Flow: " + resume.getFlowName());
        System.out.println("State: " + (resume.isSuccessfulExecution() ? "Successful" : "Failed"));
        System.out.println("\nSteps:\n");

        AtomicInteger index = new AtomicInteger(0);
        resume.getStepsResults().forEach(detail -> {
            System.out.println(index.incrementAndGet() + ") Step name -> " + detail.getStepName());
            System.out.println("Was it successful? -> " + detail.isSuccessful());
            System.out.println("Response -> " + detail.getResponseString());
            System.out.print("\n");
        });
    }

    private ImpedimentTicket createTicketFromResume(FlowExecutionSummary resume) {

        String title = "Error en los servicios | " + resume.getFlowName();
        String description = resume.getStepsResults().toString();

        return ImpedimentTicket.create(title, description);
    }
}
