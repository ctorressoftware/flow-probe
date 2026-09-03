package io.github.ctorressoftware.infrastructure.json.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ctorressoftware.application.exception.JsonDeserializationException;
import io.github.ctorressoftware.application.exception.JsonExtractionException;
import io.github.ctorressoftware.application.exception.JsonSerializationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class JacksonJsonProcessorTest {

    private ObjectMapper objectMapper;

    private JacksonJsonProcessor jacksonJsonProcessor;

    @Test
    void shouldReturnSerializedText() {
        objectMapper = new ObjectMapper();
        jacksonJsonProcessor = new JacksonJsonProcessor(objectMapper);

        String expected = """
                "{\\n  \\"user_id\\": 84920,\\n  \\"username\\": \\"johndoe\\",\\n  \\"is_active\\": true,\\n  \\"balance\\": 145.50,\\n  \\"profile\\": {\\n    \\"first_name\\": \\"John\\",\\n    \\"last_name\\": \\"Doe\\",\\n    \\"email\\": \\"john.doe@example.com\\",\\n    \\"phone\\": \\"+1-555-0198\\"\\n  },\\n  \\"preferences\\": {\\n    \\"theme\\": \\"dark\\",\\n    \\"notifications\\": {\\n      \\"email\\": true,\\n      \\"sms\\": false\\n    },\\n    \\"language\\": \\"en-US\\"\\n  },\\n  \\"recent_orders\\": [\\n    {\\n      \\"order_id\\": \\"ORD-9381\\",\\n      \\"total\\": 49.99\\n    }\\n  ],\\n  \\"metadata\\": null\\n}"
                """.stripTrailing();

        String serialized = jacksonJsonProcessor.serialize("""
                {
                  "user_id": 84920,
                  "username": "johndoe",
                  "is_active": true,
                  "balance": 145.50,
                  "profile": {
                    "first_name": "John",
                    "last_name": "Doe",
                    "email": "john.doe@example.com",
                    "phone": "+1-555-0198"
                  },
                  "preferences": {
                    "theme": "dark",
                    "notifications": {
                      "email": true,
                      "sms": false
                    },
                    "language": "en-US"
                  },
                  "recent_orders": [
                    {
                      "order_id": "ORD-9381",
                      "total": 49.99
                    }
                  ],
                  "metadata": null
                }
                """.stripTrailing());

        Assertions.assertEquals(expected, serialized);
    }

    @Test
    void shouldWrapJsonProcessingExceptionAsJsonSerializationException()
            throws JsonProcessingException {

        objectMapper = Mockito.mock(ObjectMapper.class);
        jacksonJsonProcessor = new JacksonJsonProcessor(objectMapper);

        JsonProcessingException cause =
                Mockito.mock(JsonProcessingException.class);

        Mockito
                .when(objectMapper.writeValueAsString(Mockito.anyString()))
                .thenThrow(cause);

        JsonSerializationException exception = assertThrows(
                JsonSerializationException.class,
                () -> jacksonJsonProcessor.serialize(Mockito.anyString())
        );

        Assertions.assertSame(cause, exception.getCause());

        Mockito.verify(objectMapper, Mockito.times(1))
                .writeValueAsString(Mockito.anyString());

        Assertions.assertEquals(
                "Could not serialize data to JSON",
                exception.getMessage()
        );
    }

    @Test
    void shouldWrapJsonProcessingExceptionAsJsonExtractionException()
            throws JsonProcessingException {

        objectMapper = Mockito.mock(ObjectMapper.class);
        jacksonJsonProcessor = new JacksonJsonProcessor(objectMapper);

        JsonProcessingException cause =
                Mockito.mock(JsonProcessingException.class);

        String json = """
                {"username": "password"}
                """.stripTrailing();

        Mockito
                .when(objectMapper.readTree(Mockito.anyString()))
                .thenThrow(cause);

        JsonExtractionException exception = assertThrows(
                JsonExtractionException.class,
                () -> jacksonJsonProcessor.extractValue(
                        json,
                        "username"
                )
        );

        Assertions.assertSame(cause, exception.getCause());

        Mockito.verify(objectMapper, Mockito.times(1))
                .readTree(Mockito.anyString());

        Assertions.assertEquals(
                "Could not extract data from JSON",
                exception.getMessage()
        );
    }

    @Test
    void shouldReturnMapFromJsonString() {

        objectMapper = new ObjectMapper();
        jacksonJsonProcessor = new JacksonJsonProcessor(objectMapper);

        Map<String, String> expected = Map.of("username", "password");

        Map<String, String> result = jacksonJsonProcessor.readStringMap("""
                {"username": "password"}
                """.stripTrailing());

        Assertions.assertEquals(expected, result);
    }

    @Test
    void shouldWrapJsonProcessingExceptionAsJsonDeserializationException()
            throws JsonProcessingException {

        objectMapper = Mockito.mock(ObjectMapper.class);
        jacksonJsonProcessor = new JacksonJsonProcessor(objectMapper);

        JsonProcessingException cause =
                Mockito.mock(JsonProcessingException.class);

        String json = "{\"username\":\"password\"}";

        Mockito
                .when(objectMapper.readValue(
                        Mockito.eq(json),
                        ArgumentMatchers.<TypeReference<Map<String, String>>>any()
                ))
                .thenThrow(cause);

        JsonDeserializationException exception = assertThrows(
                JsonDeserializationException.class,
                () -> jacksonJsonProcessor.readStringMap(json)
        );

        Assertions.assertSame(cause, exception.getCause());

        Mockito.verify(objectMapper, Mockito.times(1))
                .readValue(
                        Mockito.eq(json),
                        ArgumentMatchers.<TypeReference<Map<String, String>>>any()
                );

        Assertions.assertEquals(
                "Could not read deserialize data from JSON",
                exception.getMessage()
        );
    }

    @Test
    void shouldThrowJsonExtractionExceptionIfJsonNodeIsMissing() {

        objectMapper = new ObjectMapper();
        jacksonJsonProcessor = new JacksonJsonProcessor(objectMapper);

        String json = """
            {
              "user": {
                "name": "John"
              }
            }
            """;

        String missingPath = "/user/email";

        JsonExtractionException exception = Assertions.assertThrows(
                JsonExtractionException.class,
                () -> jacksonJsonProcessor.extractValue(json, missingPath)
        );

        Assertions.assertEquals(
                "JSON path does not exist: " + missingPath,
                exception.getMessage()
        );
    }
}
