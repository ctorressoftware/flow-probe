package io.github.ctorressoftware.infrastructure.cli;

import io.github.ctorressoftware.application.port.in.provider.configure.ConfigureProviderCommand;
import io.github.ctorressoftware.application.port.in.provider.configure.ConfigureProviderResult;
import io.github.ctorressoftware.application.port.in.provider.configure.ConfigureProviderUseCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import picocli.CommandLine;

import java.io.PrintStream;

@ExtendWith(MockitoExtension.class)
class ConfigureCommandTest {

    @Mock
    private PrintStream out;

    @Mock
    private ConfigureProviderUseCase configureProviderUseCase;

    private ConfigureCommand configureCommand;

    @BeforeEach
    void init() {
        configureCommand = new ConfigureCommand(out, configureProviderUseCase);
    }

    @Test
    void shouldConfigureProviderSuccessfully() {

        FlowProbeCommand rootCommand = new FlowProbeCommand();
        CommandLine cmd = new CommandLine(rootCommand);
        cmd.addSubcommand("configure", configureCommand);

        Mockito
                .when(configureProviderUseCase.configure(Mockito.any(ConfigureProviderCommand.class)))
                .thenReturn(new ConfigureProviderResult(true));

        int exitCode = cmd.execute("configure",  "azure");

        Assertions.assertEquals(ExitCode.SUCCESS.code(), exitCode);
    }
}
