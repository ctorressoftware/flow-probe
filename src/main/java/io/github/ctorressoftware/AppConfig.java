package io.github.ctorressoftware;

import io.github.ctorressoftware.application.port.in.flowexecution.ExecuteFlowUseCase;
import io.github.ctorressoftware.application.port.in.readfile.ReadFileUseCase;
import io.github.ctorressoftware.application.port.out.FlowFileReader;
import io.github.ctorressoftware.application.port.out.ServiceCaller;
import io.github.ctorressoftware.application.usecase.ReadFileHandler;
import io.github.ctorressoftware.application.usecase.flowexecution.ExecuteFlowHandler;
import io.github.ctorressoftware.application.usecase.flowexecution.FlowExecutor;
import io.github.ctorressoftware.domain.model.Context;
import io.github.ctorressoftware.infrastructure.callservice.RestServiceCaller;
import io.github.ctorressoftware.infrastructure.readfile.YAMLReader;

public final class AppConfig {
    private final FlowFileReader flowFileReader = new YAMLReader();
    private final ReadFileUseCase readFileUseCase = new ReadFileHandler(flowFileReader);
    private final Context context = new Context();
    private final ServiceCaller serviceCaller = new RestServiceCaller();
    private final FlowExecutor flowExecutor = new FlowExecutor(context, serviceCaller);
    private final ExecuteFlowUseCase executeFlowUseCase = new ExecuteFlowHandler(flowExecutor);

    public ReadFileUseCase readFileUseCase() {
        return readFileUseCase;
    }

    public ExecuteFlowUseCase executeFlowUseCase() {
        return executeFlowUseCase;
    }
}
