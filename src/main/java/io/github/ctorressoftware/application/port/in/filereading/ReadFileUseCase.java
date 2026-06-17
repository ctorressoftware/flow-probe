package io.github.ctorressoftware.application.port.in.filereading;

public interface ReadFileUseCase {
    ReadFileResult read(ReadFileCommand command);
}
