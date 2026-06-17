package io.github.ctorressoftware;

import io.github.ctorressoftware.application.port.in.readfile.ReadFileUseCase;
import io.github.ctorressoftware.application.port.out.FlowFileReader;
import io.github.ctorressoftware.application.usecase.ReadFileHandler;
import io.github.ctorressoftware.infrastructure.filereading.YAMLReader;

public final class AppConfig {
    private final FlowFileReader flowFileReader = new YAMLReader();
    private final ReadFileUseCase readFileUseCase = new ReadFileHandler(flowFileReader);

    public ReadFileUseCase readFileUseCase() {
        return readFileUseCase;
    }
}
