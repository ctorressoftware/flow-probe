package io.github.ctorressoftware;

import io.github.ctorressoftware.infrastructure.cli.ConfigureCommand;
import io.github.ctorressoftware.infrastructure.cli.FlowProbeCommand;
import io.github.ctorressoftware.infrastructure.cli.RunCommand;
import picocli.CommandLine;

public class Main {
    public static void main(String[] args) {
        AppConfig config = new AppConfig();
        FlowProbeCommand rootCommand = new FlowProbeCommand();
        CommandLine commandLine = new CommandLine(rootCommand);

        commandLine.addSubcommand("run", new RunCommand(
                config.scanner(),
                config.requestRenderer(),
                config.readFileUseCase(),
                config.executeFlowUseCase(),
                config.createImpedimentTicketUseCase()
        ));

        commandLine.addSubcommand("configure", new ConfigureCommand(
                config.configureProviderUseCase()
        ));

        int exitCode = commandLine.execute(args);
        System.exit(exitCode);
    }
}