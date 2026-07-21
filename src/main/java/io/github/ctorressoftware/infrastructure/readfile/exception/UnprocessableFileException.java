package io.github.ctorressoftware.infrastructure.readfile.exception;

public class UnprocessableFileException extends RuntimeException {
    public UnprocessableFileException(String filepath) {
        super("Unprocessable file format. Filepath: " + filepath);
    }
}
