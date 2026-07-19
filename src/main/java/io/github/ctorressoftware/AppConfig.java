package io.github.ctorressoftware;

import io.github.ctorressoftware.application.port.in.createticket.CreateImpedimentTicketUseCase;
import io.github.ctorressoftware.application.port.in.flowexecution.ExecuteFlowUseCase;
import io.github.ctorressoftware.application.port.in.provider.configure.ConfigureProviderUseCase;
import io.github.ctorressoftware.application.port.in.provider.configure.Provider;
import io.github.ctorressoftware.application.port.in.readfile.ReadFileUseCase;
import io.github.ctorressoftware.application.port.out.*;
import io.github.ctorressoftware.application.usecase.CreateImpedimentTicketHandler;
import io.github.ctorressoftware.application.usecase.ReadFileHandler;
import io.github.ctorressoftware.application.usecase.flowexecution.ExecuteFlowHandler;
import io.github.ctorressoftware.application.usecase.flowexecution.FlowExecutor;
import io.github.ctorressoftware.application.usecase.provider.configure.ConfigureProviderHandler;
import io.github.ctorressoftware.domain.model.Context;
import io.github.ctorressoftware.infrastructure.callservice.RestServiceCaller;
import io.github.ctorressoftware.infrastructure.persistence.adapter.KeystoreProviderConfigRepositoryAdapter;
import io.github.ctorressoftware.infrastructure.persistence.keystore.KeystoreCredentialsStorageManager;
import io.github.ctorressoftware.infrastructure.provider.azure.AzureProviderConfigurator;
import io.github.ctorressoftware.infrastructure.provider.azure.AzureProviderPrompt;
import io.github.ctorressoftware.infrastructure.readfile.yaml.YamlReader;
import io.github.ctorressoftware.infrastructure.renderer.CurlRequestRenderer;
import io.github.ctorressoftware.infrastructure.ticket.azuredevops.AzureDevOpsImpedimentTicketCreatorAdapter;
import io.github.ctorressoftware.infrastructure.ticket.azuredevops.AzureDevOpsWorkItemClient;
import io.github.ctorressoftware.infrastructure.ticket.azuredevops.AzureDevOpsWorkItemTicketCreator;

import java.util.Map;
import java.util.Scanner;

public final class AppConfig {
    private final RequestRenderer requestRenderer = new CurlRequestRenderer();
    private final Scanner scanner = new Scanner(System.in);
    private final FlowFileReader flowFileReader = new YamlReader();
    private final ReadFileUseCase readFileUseCase = new ReadFileHandler(flowFileReader);
    private final Context context = new Context();
    private final ServiceCaller serviceCaller = new RestServiceCaller();
    private final FlowExecutor flowExecutor = new FlowExecutor(context, serviceCaller);
    private final ExecuteFlowUseCase executeFlowUseCase = new ExecuteFlowHandler(flowExecutor);
    private final AzureDevOpsWorkItemClient azureDevOpsWorkItemClient = new AzureDevOpsWorkItemClient();
    private final CredentialsStorageManager credentialsStorageManager = new KeystoreCredentialsStorageManager();
    private final ProviderConfigRepository providerConfigRepository = new KeystoreProviderConfigRepositoryAdapter(credentialsStorageManager);
    private final AzureDevOpsWorkItemTicketCreator azureDevOpsWorkItemTicketCreator = new AzureDevOpsWorkItemTicketCreator(azureDevOpsWorkItemClient, providerConfigRepository);
    private final ImpedimentTicketCreator impedimentTicketCreator = new AzureDevOpsImpedimentTicketCreatorAdapter(azureDevOpsWorkItemTicketCreator);
    private final CreateImpedimentTicketUseCase createImpedimentTicketUseCase = new CreateImpedimentTicketHandler(impedimentTicketCreator);
    private final ProviderPrompt azurePrompt = new AzureProviderPrompt(scanner);
    private final ProviderConfigurator azureConfigurator = new AzureProviderConfigurator(providerConfigRepository);
    private final ConfigureProviderUseCase configureProviderUseCase = new ConfigureProviderHandler(
            Map.of(Provider.AZURE, azureConfigurator),
            Map.of(Provider.AZURE, azurePrompt)
    );

    public Scanner scanner() {
        return scanner;
    }

    public ReadFileUseCase readFileUseCase() {
        return readFileUseCase;
    }

    public ExecuteFlowUseCase executeFlowUseCase() {
        return executeFlowUseCase;
    }

    public CreateImpedimentTicketUseCase createImpedimentTicketUseCase() {
        return createImpedimentTicketUseCase;
    }

    public RequestRenderer requestRenderer() {
        return requestRenderer;
    }

    public ConfigureProviderUseCase configureProviderUseCase() {
        return configureProviderUseCase;
    }
}
