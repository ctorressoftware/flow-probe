package io.github.ctorressoftware.infrastructure.json.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ctorressoftware.application.port.out.JsonProcessor;

public class JacksonJsonProcessor implements JsonProcessor {

    private final ObjectMapper objectMapper;

    public JacksonJsonProcessor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String serialize(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not serialize request body to JSON", e); // TODO: customize
        }
    }

    @Override
    public String extractValue(String data, String valuePath) {
        try {
            JsonNode root = objectMapper.readTree(data);
            return root.at(valuePath).asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e); // TODO: customize
        }
    }
}
