package io.github.ctorressoftware.infrastructure.cli;

import io.github.ctorressoftware.application.port.in.createticket.CreateImpedimentTicketCommand;
import io.github.ctorressoftware.application.port.in.createticket.CreateImpedimentTicketUseCase;
import io.github.ctorressoftware.application.port.in.flowexecution.ExecuteFlowCommand;
import io.github.ctorressoftware.application.port.in.flowexecution.ExecuteFlowResult;
import io.github.ctorressoftware.application.port.in.flowexecution.ExecuteFlowUseCase;
import io.github.ctorressoftware.application.port.in.readfile.ReadFileCommand;
import io.github.ctorressoftware.application.port.in.readfile.ReadFileResult;
import io.github.ctorressoftware.application.port.in.readfile.ReadFileUseCase;
import io.github.ctorressoftware.domain.model.ExecutionResume;
import io.github.ctorressoftware.domain.model.FilePath;
import io.github.ctorressoftware.domain.model.Flow;
import io.github.ctorressoftware.domain.model.ImpedimentTicket;
import picocli.CommandLine;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public class RunCommand implements Callable<Integer> {

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

    private final ReadFileUseCase readFileUseCase;
    private final ExecuteFlowUseCase executeFlowUseCase;
    private final CreateImpedimentTicketUseCase createImpedimentTicketUseCase;

    public RunCommand(
        ReadFileUseCase readFileUseCase, 
        ExecuteFlowUseCase executeFlowUseCase,
        CreateImpedimentTicketUseCase createImpedimentTicketUseCase
    ) {
        this.readFileUseCase = readFileUseCase;
        this.executeFlowUseCase = executeFlowUseCase;
        this.createImpedimentTicketUseCase = createImpedimentTicketUseCase;
    }

    @Override
    public Integer call() {
        ReadFileResult readFileResult = readFileUseCase.read(new ReadFileCommand(new FilePath(filePath)));
        Flow flow = readFileResult.flow();
        ExecuteFlowResult executeFlowResult = executeFlowUseCase.execute(new ExecuteFlowCommand(flow));
        ExecutionResume resume = executeFlowResult.resume();
        printFlowResume(resume);

        if (resume.isSuccessfulExecution()) {
            ImpedimentTicket ticket = createTicketFromResume(resume);
            createImpedimentTicketUseCase.createTicket(new CreateImpedimentTicketCommand(ticket));
        }

        return 0;
    }

    private void printFlowResume(ExecutionResume resume) {
        System.out.println("Flow: " + resume.getFlowName());
        System.out.println("State: " + (resume.isSuccessfulExecution() ? "Successful" : "Failed"));
        System.out.println("\nSteps:\n");

        AtomicInteger index = new AtomicInteger(0);
        resume.getStepsResults().forEach(detail -> {
            System.out.println(index.incrementAndGet() + ") Step name -> " + detail.getStepName());
            System.out.println("Was it successful? -> " + detail.isSuccessful());
            System.out.println("Response -> " + detail.getResponseString().substring(0, 100));
            System.out.print("\n");
        });
    }

    private ImpedimentTicket createTicketFromResume(ExecutionResume resume) {

        String title = "Error en los servicios | " + resume.getFlowName();
        String description = resume.getStepsResults().toString();

        return new ImpedimentTicket(title, description);
    }
}
