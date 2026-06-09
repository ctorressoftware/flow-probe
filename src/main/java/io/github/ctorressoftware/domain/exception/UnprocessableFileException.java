package io.github.ctorressoftware.domain.exception;

public class UnprocessableFileException extends RuntimeException {
    public UnprocessableFileException(String filepath) {
        super("Unprocessable file format. Filepath: " + filepath);
    }
}
