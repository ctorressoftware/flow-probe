package io.github.ctorressoftware.infrastructure.cli;

import io.github.ctorressoftware.infrastructure.cli.provider.VersionProvider;
import picocli.CommandLine;

@CommandLine.Command(
        name = "flowprobe",
        mixinStandardHelpOptions = true,
        description = "CLI tool to execute and verify HTTP flows",
        versionProvider = VersionProvider.class
)
public class FlowProbeCommand implements Runnable {

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }
}