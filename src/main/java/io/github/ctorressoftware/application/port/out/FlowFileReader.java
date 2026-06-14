package io.github.ctorressoftware.application.port.out;

import io.github.ctorressoftware.domain.model.FilePath;
import io.github.ctorressoftware.domain.model.FlowStep;

import java.util.List;

public interface FlowFileReader {
    List<FlowStep> read(FilePath filePath);
}
