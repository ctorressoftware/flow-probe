package io.github.ctorressoftware.application.port.out;

public interface JsonProcessor {
    String serialize(Object value);
    String extractValue(String json, String path);
}
