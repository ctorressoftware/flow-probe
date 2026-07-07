package io.github.ctorressoftware.infrastructure.cli;

import io.github.ctorressoftware.application.port.in.provider.configure.ConfigureProviderCommand;
import io.github.ctorressoftware.application.port.in.provider.configure.ConfigureProviderResult;
import io.github.ctorressoftware.application.port.in.provider.configure.ConfigureProviderUseCase;
import io.github.ctorressoftware.application.port.in.provider.configure.Provider;
import picocli.CommandLine;

@CommandLine.Command(name = "configure")
public class ConfigureCommand implements Runnable {

    private final ConfigureProviderUseCase configureProviderUseCase;

    @CommandLine.Parameters(index = "0")
    private String provider;

    public ConfigureCommand(ConfigureProviderUseCase configureProviderUseCase) {
        this.configureProviderUseCase = configureProviderUseCase;
    }

    @Override
    public void run() {
        Provider providerValue = Provider.valueOf(provider.toUpperCase());
        ConfigureProviderResult result = configureProviderUseCase
                .configure(new ConfigureProviderCommand(providerValue.name()));
        System.out.println(result.configured());
    }
}

