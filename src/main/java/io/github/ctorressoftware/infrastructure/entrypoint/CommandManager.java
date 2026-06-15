package io.github.ctorressoftware.infrastructure.entrypoint;

import io.github.ctorressoftware.AppConfig;
import io.github.ctorressoftware.ExecutionPipeline;
import io.github.ctorressoftware.application.port.in.readfile.ReadFileCommand;
import io.github.ctorressoftware.application.port.in.readfile.ReadFileResult;
import io.github.ctorressoftware.application.port.in.readfile.ReadFileUseCase;
import io.github.ctorressoftware.domain.model.FilePath;
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

    private final AppConfig config;

    public CommandManager(AppConfig config) {
        this.config = config;
    }

    @Override
    public void run() {
        ReadFileUseCase readFileHandler = config.readFileUseCase();
        ReadFileResult readFileResult = readFileHandler.read(new ReadFileCommand(new FilePath(filePath)));
        List<FlowStep> steps = readFileResult.flowSteps();

        String flowName = steps.getFirst().getFlowName();
        var executionPipeline = new ExecutionPipeline();
        executionPipeline.execute(flowName, steps);
    }
}