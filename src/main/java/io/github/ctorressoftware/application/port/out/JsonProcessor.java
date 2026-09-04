package io.github.ctorressoftware.application.port.out;

import java.util.Map;

public interface JsonProcessor {
    String serialize(Object value);
    Object extractValue(String json, String path);
    Map<String, String> readStringMap(String json);
}
