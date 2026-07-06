package io.github.ctorressoftware.infrastructure.cli;

import picocli.CommandLine;

@CommandLine.Command(name = "configure")
public class ConfigureCommand implements Runnable {

    @CommandLine.Parameters(index = "0")
    private String provider;

    public ConfigureCommand() {}

    @Override
    public void run() {
        System.out.println(provider); // TODO: make the logic
    }
}

