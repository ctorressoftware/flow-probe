package io.github.ctorressoftware.infrastructure.cli;

import io.github.ctorressoftware.application.port.in.provider.configure.ConfigureProviderCommand;
import io.github.ctorressoftware.application.port.in.provider.configure.ConfigureProviderResult;
import io.github.ctorressoftware.application.port.in.provider.configure.ConfigureProviderUseCase;
import io.github.ctorressoftware.application.port.in.provider.configure.Provider;
import picocli.CommandLine;

import java.io.PrintStream;

@CommandLine.Command(name = "configure")
public class ConfigureCommand implements Runnable {

    private final PrintStream out;
    private final ConfigureProviderUseCase configureProviderUseCase;

    @CommandLine.Parameters(index = "0")
    private String provider;

    public ConfigureCommand(PrintStream out, ConfigureProviderUseCase configureProviderUseCase) {
        this.out = out;
        this.configureProviderUseCase = configureProviderUseCase;
    }

    @Override
    public void run() {
        Provider providerValue = Provider.valueOf(provider.toUpperCase());
        ConfigureProviderResult result = configureProviderUseCase
                .configure(new ConfigureProviderCommand(providerValue));
        out.println(result.configured());
    }
}

