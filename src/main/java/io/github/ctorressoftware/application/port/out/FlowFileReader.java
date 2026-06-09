package io.github.ctorressoftware.application.port.out;

import io.github.ctorressoftware.domain.model.FilePath;
import io.github.ctorressoftware.domain.model.StepFormat;

import java.util.List;

public interface FlowFileReader {
    List<StepFormat> read(FilePath filePath);
}
