package io.github.ctorressoftware.infrastructure.entrypoint;

import io.github.ctorressoftware.ExecutionPipeline;
import io.github.ctorressoftware.domain.model.FilePath;
import io.github.ctorressoftware.infrastructure.entrypoint.filereader.YAMLProcessor;
import io.github.ctorressoftware.domain.model.StepFormat;
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

    @Override
    public void run() {
        YAMLProcessor yamlProcessor = new YAMLProcessor();

        FilePath path = new FilePath(filePath);
        List<StepFormat> steps = yamlProcessor.read(path);

        String flowName = steps.getFirst().flowName();
        var executionPipeline = new ExecutionPipeline();
        executionPipeline.execute(flowName, steps);
    }
}