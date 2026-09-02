package io.github.ctorressoftware.infrastructure.json.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ctorressoftware.application.exception.JsonDeserializationException;
import io.github.ctorressoftware.application.exception.JsonExtractionException;
import io.github.ctorressoftware.application.exception.JsonSerializationException;
import io.github.ctorressoftware.application.port.out.JsonProcessor;

import java.util.Map;

public class JacksonJsonProcessor implements JsonProcessor {

    private final ObjectMapper objectMapper;

    public JacksonJsonProcessor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String serialize(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new JsonSerializationException(
                    "Could not serialize data to JSON",
                    e
            );
        }
    }

    @Override
    public String extractValue(String data, String valuePath) {
        try {
            JsonNode root = objectMapper.readTree(data);
            JsonNode node = root.at(valuePath);

            if (node.isMissingNode()) {
                throw new JsonExtractionException(
                        "JSON path does not exist: " + valuePath
                );
            }
            return node.asText();
        } catch (JsonProcessingException e) {
            throw new JsonExtractionException(
                    "Could not extract data from JSON",
                    e
            );
        }
    }

    @Override
    public Map<String, String> readStringMap(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new JsonDeserializationException(
                    "Could not read deserialize data from JSON",
                    e
            );
        }
    }
}
