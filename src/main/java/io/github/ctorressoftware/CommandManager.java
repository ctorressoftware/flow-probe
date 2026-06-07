package io.github.ctorressoftware;

import io.github.ctorressoftware.model.StepFormat;
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
        List<StepFormat> steps = yamlProcessor.read(filePath);

        var executionPipeline = new ExecutionPipeline();
        executionPipeline.execute(steps);
    }
}