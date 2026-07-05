package io.github.ctorressoftware;

import io.github.ctorressoftware.application.port.in.createticket.CreateImpedimentTicketUseCase;
import io.github.ctorressoftware.application.port.in.flowexecution.ExecuteFlowUseCase;
import io.github.ctorressoftware.application.port.in.readfile.ReadFileUseCase;
import io.github.ctorressoftware.application.port.out.FlowFileReader;
import io.github.ctorressoftware.application.port.out.ImpedimentTicketCreator;
import io.github.ctorressoftware.application.port.out.RequestRenderer;
import io.github.ctorressoftware.application.port.out.ServiceCaller;
import io.github.ctorressoftware.application.usecase.CreateImpedimentTicketHandler;
import io.github.ctorressoftware.application.usecase.ReadFileHandler;
import io.github.ctorressoftware.application.usecase.flowexecution.ExecuteFlowHandler;
import io.github.ctorressoftware.application.usecase.flowexecution.FlowExecutor;
import io.github.ctorressoftware.domain.model.Context;
import io.github.ctorressoftware.infrastructure.callservice.RestServiceCaller;
import io.github.ctorressoftware.infrastructure.readfile.YAMLReader;
import io.github.ctorressoftware.infrastructure.renderer.CurlRequestRenderer;
import io.github.ctorressoftware.infrastructure.ticket.azuredevops.AzureDevOpsImpedimentTicketCreatorAdapter;
import io.github.ctorressoftware.infrastructure.ticket.azuredevops.AzureDevOpsWorkItemClient;
import io.github.ctorressoftware.infrastructure.ticket.azuredevops.AzureDevOpsWorkItemTicketCreator;

import java.util.Scanner;

public final class AppConfig {
    private final RequestRenderer requestRenderer = new CurlRequestRenderer();
    private final Scanner scanner = new Scanner(System.in);
    private final FlowFileReader flowFileReader = new YAMLReader();
    private final ReadFileUseCase readFileUseCase = new ReadFileHandler(flowFileReader);
    private final Context context = new Context();
    private final ServiceCaller serviceCaller = new RestServiceCaller();
    private final FlowExecutor flowExecutor = new FlowExecutor(context, serviceCaller);
    private final ExecuteFlowUseCase executeFlowUseCase = new ExecuteFlowHandler(flowExecutor);
    private final AzureDevOpsWorkItemClient azureDevOpsWorkItemClient = new AzureDevOpsWorkItemClient();
    private final AzureDevOpsWorkItemTicketCreator azureDevOpsWorkItemTicketCreator = new AzureDevOpsWorkItemTicketCreator(azureDevOpsWorkItemClient);
    private final ImpedimentTicketCreator impedimentTicketCreator = new AzureDevOpsImpedimentTicketCreatorAdapter(azureDevOpsWorkItemTicketCreator);
    private final CreateImpedimentTicketUseCase createImpedimentTicketUseCase = new CreateImpedimentTicketHandler(impedimentTicketCreator);

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
}
