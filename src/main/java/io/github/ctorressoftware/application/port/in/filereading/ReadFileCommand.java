package io.github.ctorressoftware.application.port.in.filereading;

import io.github.ctorressoftware.domain.model.FilePath;

public record ReadFileCommand(FilePath filePath) {}
