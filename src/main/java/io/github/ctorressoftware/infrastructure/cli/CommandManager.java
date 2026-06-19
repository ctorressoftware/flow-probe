package io.github.ctorressoftware.infrastructure.cli;

import io.github.ctorressoftware.AppConfig;
import io.github.ctorressoftware.ExecutionPipeline;
import io.github.ctorressoftware.application.port.in.flowexecution.ExecuteFlowCommand;
import io.github.ctorressoftware.application.port.in.flowexecution.ExecuteFlowResult;
import io.github.ctorressoftware.application.port.in.flowexecution.ExecuteFlowUseCase;
import io.github.ctorressoftware.application.port.in.readfile.ReadFileCommand;
import io.github.ctorressoftware.application.port.in.readfile.ReadFileResult;
import io.github.ctorressoftware.application.port.in.readfile.ReadFileUseCase;
import io.github.ctorressoftware.domain.model.ExecutionResume;
import io.github.ctorressoftware.domain.model.FilePath;
import io.github.ctorressoftware.domain.model.Flow;
import io.github.ctorressoftware.domain.model.FlowStep;
import picocli.CommandLine;

import java.util.List;

@CommandLine.Command(name = "FlowProbe", version = "FlowProbe 1.0", mixinStandardHelpOptions = true)
public class CommandManager implements Runnable {

    @CommandLine.Option(
            names = {"-f", "-file"},
            required = true,
            paramLabel = "FILE",
            description = "file url"
    )
    String filePath;

    private final ReadFileUseCase readFileUseCase;
    private final ExecuteFlowUseCase executeFlowUseCase;

    public CommandManager(AppConfig config) {
        this.readFileUseCase = config.readFileUseCase();
        this.executeFlowUseCase = config.executeFlowUseCase();
    }

    @Override
    public void run() {
        ReadFileResult readFileResult = readFileUseCase.read(new ReadFileCommand(new FilePath(filePath)));
        Flow flow = readFileResult.flow();
        ExecuteFlowResult executeFlowResult = executeFlowUseCase.execute(new ExecuteFlowCommand(flow));

        ExecutionResume resume = executeFlowResult.resume();
        printFlowResume(resume);
    }

    private void printFlowResume(ExecutionResume resume) {
        System.out.println("Flow: " + resume.getFlowName());
        System.out.println("State: " + (resume.isSuccessfulExecution() ? "Successful" : "Failed"));
    }
}