package io.github.ctorressoftware.infrastructure.cli;

import io.github.ctorressoftware.AppConfig;
import io.github.ctorressoftware.ExecutionPipeline;
import io.github.ctorressoftware.application.port.in.filereading.ReadFileCommand;
import io.github.ctorressoftware.application.port.in.filereading.ReadFileResult;
import io.github.ctorressoftware.application.port.in.filereading.ReadFileUseCase;
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

    private final ReadFileUseCase readFileHandler;

    public CommandManager(AppConfig config) {
        this.readFileHandler = config.readFileUseCase();
    }

    @Override
    public void run() {
        ReadFileResult readFileResult = readFileHandler.read(new ReadFileCommand(new FilePath(filePath)));
        Flow flow = readFileResult.flow();
        List<FlowStep> flowSteps = flow.getSteps();

        String flowName = flow.getName();
        var executionPipeline = new ExecutionPipeline();
        executionPipeline.execute(flowName, flowSteps);
    }
}