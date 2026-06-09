package io.github.ctorressoftware.exceptions;

public class UnprocessableFileException extends RuntimeException {
    public UnprocessableFileException(String filepath) {
        super("Unprocessable file format. Filepath: " + filepath);
    }
}
