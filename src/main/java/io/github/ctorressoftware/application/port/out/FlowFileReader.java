package io.github.ctorressoftware.application.port.out;

import io.github.ctorressoftware.domain.model.FilePath;
import io.github.ctorressoftware.domain.model.Flow;

public interface FlowFileReader {
    Flow read(FilePath filePath);
}
