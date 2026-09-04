package io.github.ctorressoftware;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javakeyring.Keyring;
import io.github.ctorressoftware.application.port.in.createticket.CreateImpedimentTicketUseCase;
import io.github.ctorressoftware.application.port.in.flowexecution.ExecuteFlowUseCase;
import io.github.ctorressoftware.application.port.in.provider.configure.ConfigureProviderUseCase;
import io.github.ctorressoftware.application.port.in.provider.configure.Provider;
import io.github.ctorressoftware.application.port.in.readfile.ReadFileUseCase;
import io.github.ctorressoftware.application.port.out.*;
import io.github.ctorressoftware.application.usecase.CreateImpedimentTicketHandler;
import io.github.ctorressoftware.application.usecase.ExecuteFlowHandler;
import io.github.ctorressoftware.application.usecase.ReadFileHandler;
import io.github.ctorressoftware.application.usecase.flowexecution.*;
import io.github.ctorressoftware.application.usecase.flowexecution.validation.BodyValidator;
import io.github.ctorressoftware.application.usecase.flowexecution.validation.DefaultResponseValidator;
import io.github.ctorressoftware.application.usecase.flowexecution.validation.ResponseValidator;
import io.github.ctorressoftware.application.usecase.flowexecution.validation.StatusValidator;
import io.github.ctorressoftware.application.usecase.flowexecution.validation.evaluator.EqualsExpectationEvaluator;
import io.github.ctorressoftware.application.usecase.flowexecution.validation.evaluator.ExpectationEvaluator;
import io.github.ctorressoftware.application.usecase.flowexecution.validation.evaluator.ExpectationEvaluatorRegistry;
import io.github.ctorressoftware.application.usecase.flowexecution.validation.evaluator.NotEqualsExpectationEvaluator;
import io.github.ctorressoftware.application.usecase.provider.configure.ConfigureProviderHandler;
import io.github.ctorressoftware.domain.model.Context;
import io.github.ctorressoftware.infrastructure.callservice.RequestMapper;
import io.github.ctorressoftware.infrastructure.callservice.RestServiceCaller;
import io.github.ctorressoftware.infrastructure.json.jackson.JacksonJsonProcessor;
import io.github.ctorressoftware.infrastructure.persistence.adapter.KeystoreProviderConfigRepositoryAdapter;
import io.github.ctorressoftware.infrastructure.persistence.keystore.KeyringFactory;
import io.github.ctorressoftware.infrastructure.persistence.keystore.KeystoreCredentialsStorageManager;
import io.github.ctorressoftware.infrastructure.provider.azure.AzureProviderConfigurator;
import io.github.ctorressoftware.infrastructure.provider.azure.AzureProviderPrompt;
import io.github.ctorressoftware.infrastructure.readfile.yaml.YamlReader;
import io.github.ctorressoftware.infrastructure.renderer.CurlRequestRenderer;
import io.github.ctorressoftware.infrastructure.ticket.azuredevops.AzureDevOpsImpedimentTicketCreatorAdapter;
import io.github.ctorressoftware.infrastructure.ticket.azuredevops.AzureDevOpsWorkItemClient;
import io.github.ctorressoftware.infrastructure.ticket.azuredevops.AzureDevOpsWorkItemTicketCreator;

import java.io.PrintStream;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public final class AppConfig {
    private static final Duration HTTP_CONNECT_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration HTTP_REQUEST_TIMEOUT = Duration.ofSeconds(30);
    private final PrintStream out = System.out;
    private final Context context = new Context(); // TODO: check if could be a bug
    private final Scanner scanner = new Scanner(System.in);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final KeyringFactory keyringFactory = Keyring::create;
    private final JsonProcessor jsonProcessor = new JacksonJsonProcessor(objectMapper);
    private final RequestRenderer requestRenderer = new CurlRequestRenderer(jsonProcessor);
    private final FlowFileReader flowFileReader = new YamlReader();
    private final PlaceholderResolver placeholderResolver = new PlaceholderResolver();
    private final ReadFileUseCase readFileUseCase = new ReadFileHandler(flowFileReader);
    private final ContextManager contextManager = new ContextManager(context, jsonProcessor);
    private final ExpectationEvaluator equalsExpectationEvaluator = new EqualsExpectationEvaluator();
    private final RequestMapper requestMapper = new RequestMapper(jsonProcessor, HTTP_REQUEST_TIMEOUT);
    private final ExpectationEvaluator notEqualsExpectationEvaluator = new NotEqualsExpectationEvaluator();
    private final ExpectationEvaluatorRegistry registry = new ExpectationEvaluatorRegistry(List.of(
            equalsExpectationEvaluator,
            notEqualsExpectationEvaluator
    ));
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(HTTP_CONNECT_TIMEOUT)
            .build();
    private final ServiceCaller serviceCaller = new RestServiceCaller(httpClient, requestMapper);
    private final StatusValidator statusValidator = new StatusValidator(registry);
    private final BodyValidator bodyValidator = new BodyValidator(jsonProcessor, registry);
    private final ResponseValidator responseValidator = new DefaultResponseValidator(statusValidator, bodyValidator);
    private final Executor executor = new FlowExecutor(contextManager, serviceCaller, placeholderResolver, responseValidator);
    private final ExecuteFlowUseCase executeFlowUseCase = new ExecuteFlowHandler(executor);
    private final CredentialsStorageManager credentialsStorageManager = new KeystoreCredentialsStorageManager(keyringFactory);
    private final AzureDevOpsWorkItemClient azureDevOpsWorkItemClient = new AzureDevOpsWorkItemClient(httpClient, HTTP_REQUEST_TIMEOUT);
    private final ProviderConfigRepository providerConfigRepository = new KeystoreProviderConfigRepositoryAdapter(jsonProcessor, credentialsStorageManager);
    private final AzureDevOpsWorkItemTicketCreator azureDevOpsWorkItemTicketCreator = new AzureDevOpsWorkItemTicketCreator(azureDevOpsWorkItemClient, providerConfigRepository);
    private final ImpedimentTicketCreator impedimentTicketCreator = new AzureDevOpsImpedimentTicketCreatorAdapter(azureDevOpsWorkItemTicketCreator);
    private final CreateImpedimentTicketUseCase createImpedimentTicketUseCase = new CreateImpedimentTicketHandler(impedimentTicketCreator);
    private final ProviderPrompt azurePrompt = new AzureProviderPrompt(out, scanner, System.console());
    private final ProviderConfigurator azureConfigurator = new AzureProviderConfigurator(providerConfigRepository);
    private final ConfigureProviderUseCase configureProviderUseCase = new ConfigureProviderHandler(
            Map.of(Provider.AZURE, azureConfigurator),
            Map.of(Provider.AZURE, azurePrompt)
    );

    public PrintStream out() {
        return out;
    }

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
