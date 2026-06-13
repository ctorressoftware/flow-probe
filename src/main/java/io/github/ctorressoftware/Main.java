package io.github.ctorressoftware;

import io.github.ctorressoftware.infrastructure.entrypoint.CommandManager;
import picocli.CommandLine;

public class Main {
    public static void main(String[] args) {
        int exitCode = new CommandLine(new CommandManager()).execute(args);
        System.exit(exitCode);
    }
}