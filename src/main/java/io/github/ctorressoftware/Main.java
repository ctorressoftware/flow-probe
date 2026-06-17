package io.github.ctorressoftware;

import io.github.ctorressoftware.infrastructure.cli.CommandManager;
import picocli.CommandLine;

public class Main {
    public static void main(String[] args) {
        final AppConfig config = new AppConfig();
        final CommandManager commandManager = new CommandManager(config);
        int exitCode = new CommandLine(commandManager).execute(args);
        System.exit(exitCode);
    }
}