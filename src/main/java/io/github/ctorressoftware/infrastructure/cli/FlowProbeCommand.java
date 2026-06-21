package io.github.ctorressoftware.infrastructure.cli;

import picocli.CommandLine;

@CommandLine.Command(
        name = "flowprobe",
        mixinStandardHelpOptions = true,
        description = "CLI tool to execute and verify HTTP flows",
        version = "flowprobe 0.1.0"
)
public class FlowProbeCommand implements Runnable {

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }
}