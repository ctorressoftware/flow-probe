package io.github.ctorressoftware.application.usecase;

import io.github.ctorressoftware.application.port.in.readfile.ReadFileCommand;
import io.github.ctorressoftware.application.port.in.readfile.ReadFileResult;
import io.github.ctorressoftware.application.port.in.readfile.ReadFileUseCase;
import io.github.ctorressoftware.application.port.out.FlowFileReader;
import io.github.ctorressoftware.domain.model.Flow;

public class ReadFileHandler implements ReadFileUseCase {

    private final FlowFileReader flowFileReader;

    public ReadFileHandler(FlowFileReader flowFileReader) {
        this.flowFileReader = flowFileReader;
    }

    @Override
    public ReadFileResult read(ReadFileCommand command) {
        Flow flow = flowFileReader.read(command.filePath());
        return new ReadFileResult(flow);
    }
}
