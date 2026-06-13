package io.github.ctorressoftware.infrastructure.entrypoint;

import io.github.ctorressoftware.ExecutionPipeline;
import io.github.ctorressoftware.domain.model.FilePath;
import io.github.ctorressoftware.infrastructure.entrypoint.filereader.YAMLReader;
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

    @Override
    public void run() {
        YAMLReader yamlReader = new YAMLReader();

        FilePath path = new FilePath(filePath);
        List<FlowStep> steps = yamlReader.read(path);

        String flowName = steps.getFirst().getFlowName();
        var executionPipeline = new ExecutionPipeline();
        executionPipeline.execute(flowName, steps);
    }
}