package io.github.ctorressoftware.infrastructure.cli;

import io.github.ctorressoftware.infrastructure.cli.provider.VersionProvider;
import picocli.CommandLine;

@CommandLine.Command(
        name = "flowprobe",
        mixinStandardHelpOptions = true,
        description = "CLI tool to execute and verify HTTP flows",
        versionProvider = VersionProvider.class,
        usageHelpAutoWidth = true,
        sortOptions = false,
        synopsisHeading = "%nUsage:%n  ",
        descriptionHeading = "%nDescription:%n  ",
        optionListHeading = "%nOptions:%n",
        commandListHeading = "%nCommands:%n",
        footer = {
                "",
                "@|bold Examples:|@",
                "  @|yellow flowprobe run --file flow.yaml|@",
                "  @|yellow flowprobe run --file flow.yaml --create-impediment|@",
                "  @|yellow flowprobe configure azure|@",
                "  @|yellow flowprobe --help|@",
                "  @|yellow flowprobe --version|@",
        }
)
public class FlowProbeCommand implements Runnable {

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }
}