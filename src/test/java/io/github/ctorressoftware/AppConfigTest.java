package io.github.ctorressoftware;

import io.github.ctorressoftware.application.port.in.createticket.CreateImpedimentTicketUseCase;
import io.github.ctorressoftware.application.port.in.flowexecution.ExecuteFlowUseCase;
import io.github.ctorressoftware.application.port.in.provider.configure.ConfigureProviderUseCase;
import io.github.ctorressoftware.application.port.in.readfile.ReadFileUseCase;
import io.github.ctorressoftware.application.port.out.RequestRenderer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.PrintStream;
import java.util.Scanner;

class AppConfigTest {

    private final AppConfig appConfig = new AppConfig();

    @Test
    void shouldProvideRequiredDependencies() {
        Assertions.assertNotNull(appConfig.out());
        Assertions.assertNotNull(appConfig.scanner());
        Assertions.assertNotNull(appConfig.readFileUseCase());
        Assertions.assertNotNull(appConfig.executeFlowUseCase());
        Assertions.assertNotNull(appConfig.createImpedimentTicketUseCase());
        Assertions.assertNotNull(appConfig.requestRenderer());
        Assertions.assertNotNull(appConfig.configureProviderUseCase());
    }

    @Test
    void shouldReturnSameDependencyInstances() {
        PrintStream printStream = appConfig.out();
        Scanner scanner = appConfig.scanner();
        ReadFileUseCase readFileUseCase = appConfig.readFileUseCase();
        ExecuteFlowUseCase executeFlowUseCase = appConfig.executeFlowUseCase();
        CreateImpedimentTicketUseCase createImpedimentTicketUseCase = appConfig.createImpedimentTicketUseCase();
        RequestRenderer requestRenderer = appConfig.requestRenderer();
        ConfigureProviderUseCase configureProviderUseCase = appConfig.configureProviderUseCase();

        Assertions.assertSame(printStream, appConfig.out());
        Assertions.assertSame(scanner, appConfig.scanner());
        Assertions.assertSame(readFileUseCase, appConfig.readFileUseCase());
        Assertions.assertSame(executeFlowUseCase, appConfig.executeFlowUseCase());
        Assertions.assertSame(createImpedimentTicketUseCase, appConfig.createImpedimentTicketUseCase());
        Assertions.assertSame(requestRenderer, appConfig.requestRenderer());
        Assertions.assertSame(configureProviderUseCase, appConfig.configureProviderUseCase());
    }

}
