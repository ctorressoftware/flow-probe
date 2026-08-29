package io.github.ctorressoftware.application.usecase;

import io.github.ctorressoftware.application.port.in.readfile.ReadFileCommand;
import io.github.ctorressoftware.application.port.in.readfile.ReadFileResult;
import io.github.ctorressoftware.application.port.out.FlowFileReader;
import io.github.ctorressoftware.domain.model.FilePath;
import io.github.ctorressoftware.domain.model.Flow;
import io.github.ctorressoftware.domain.model.FlowStep;
import io.github.ctorressoftware.domain.model.ServiceCall;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class ReadFileHandlerTest {

    @Mock
    private FlowFileReader reader;

    private ReadFileHandler handler;

    @BeforeEach
    void init() {
        handler = new ReadFileHandler(reader);
    }

    @Test
    void shouldReadSpecifiedFile() {

        FilePath filePath = new FilePath("flow.yaml");
        ReadFileCommand command = new ReadFileCommand(filePath);

        ServiceCall serviceCall = new ServiceCall(
                "https://example.com",
                "GET",
                null,
                null
        );

        Flow flow = Flow.create(
                "test-flow",
                List.of(FlowStep.create("flow", "step", serviceCall, null, null))
        );

        Mockito.when(reader.read(filePath))
                .thenReturn(flow);

        ReadFileResult result = handler.read(command);

        Assertions.assertSame(flow, result.flow());
        Mockito.verify(reader).read(filePath);
    }
}
