package io.github.ctorressoftware.application.usecase;

import io.github.ctorressoftware.application.port.in.readfile.ReadFileCommand;
import io.github.ctorressoftware.application.port.in.readfile.ReadFileResult;
import io.github.ctorressoftware.application.port.in.readfile.ReadFileUseCase;
import io.github.ctorressoftware.application.port.out.FlowFileReader;
import io.github.ctorressoftware.domain.model.FlowStep;

import java.util.List;

// TODO: implement handler in command manager
public class ReadFileHandler implements ReadFileUseCase {

    private final FlowFileReader flowFileReader;

    public ReadFileHandler(FlowFileReader flowFileReader) {
        this.flowFileReader = flowFileReader;
    }

    @Override
    public ReadFileResult read(ReadFileCommand command) {
        List<FlowStep> list = flowFileReader.read(command.filePath());
        return new ReadFileResult(list);
    }
}
