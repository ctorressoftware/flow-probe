package io.github.ctorressoftware;

import io.github.ctorressoftware.infrastructure.cli.ConfigureCommand;
import io.github.ctorressoftware.infrastructure.cli.ExitCode;
import io.github.ctorressoftware.infrastructure.cli.FlowProbeCommand;
import io.github.ctorressoftware.infrastructure.cli.RunCommand;
import picocli.CommandLine;

public class Main {
    public static void main(String[] args) {
        AppConfig config = new AppConfig();
        FlowProbeCommand rootCommand = new FlowProbeCommand();
        CommandLine commandLine = commandLine(rootCommand);

        commandLine.addSubcommand("run", new RunCommand(
                config.out(),
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

    private static CommandLine commandLine(FlowProbeCommand rootCommand) {
        CommandLine commandLine = new CommandLine(rootCommand);

        commandLine.setParameterExceptionHandler((exception, varargs) -> {
            CommandLine cmd = exception.getCommandLine();
            cmd.getErr().println("Invalid command arguments: " + exception.getMessage());
            return ExitCode.INVALID_ARGUMENTS.code();
        });
        return commandLine;
    }
}