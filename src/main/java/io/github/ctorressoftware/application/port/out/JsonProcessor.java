package io.github.ctorressoftware.application.port.out;

public interface JsonProcessor {
    String serialize(Object value);
    <T> T deserialize(String serializedJson, Class<T> dataType);
    Object extractValue(String json, String path);
}
