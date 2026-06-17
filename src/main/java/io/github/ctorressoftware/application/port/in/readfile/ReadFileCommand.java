package io.github.ctorressoftware.application.port.in.readfile;

import io.github.ctorressoftware.domain.model.FilePath;

public record ReadFileCommand(FilePath filePath) {}
