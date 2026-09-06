package io.github.ctorressoftware.infrastructure.json.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ctorressoftware.application.exception.JsonDeserializationException;
import io.github.ctorressoftware.application.exception.JsonExtractionException;
import io.github.ctorressoftware.application.exception.JsonSerializationException;
import io.github.ctorressoftware.infrastructure.ticket.azuredevops.AzureDevOpsConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class JacksonJsonProcessorTest {

    private ObjectMapper objectMapper;

    private JacksonJsonProcessor jsonProcessor;

    @Test
    void shouldReturnSerializedText() {
        objectMapper = new ObjectMapper();
        jsonProcessor = new JacksonJsonProcessor(objectMapper);

        String expected = """
                "{\\n  \\"user_id\\": 84920,\\n  \\"username\\": \\"johndoe\\",\\n  \\"is_active\\": true,\\n  \\"balance\\": 145.50,\\n  \\"profile\\": {\\n    \\"first_name\\": \\"John\\",\\n    \\"last_name\\": \\"Doe\\",\\n    \\"email\\": \\"john.doe@example.com\\",\\n    \\"phone\\": \\"+1-555-0198\\"\\n  },\\n  \\"preferences\\": {\\n    \\"theme\\": \\"dark\\",\\n    \\"notifications\\": {\\n      \\"email\\": true,\\n      \\"sms\\": false\\n    },\\n    \\"language\\": \\"en-US\\"\\n  },\\n  \\"recent_orders\\": [\\n    {\\n      \\"order_id\\": \\"ORD-9381\\",\\n      \\"total\\": 49.99\\n    }\\n  ],\\n  \\"metadata\\": null\\n}"
                """.stripTrailing();

        String serialized = jsonProcessor.serialize("""
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
    void shouldPreserveExtractedJsonValueTypes() {

        jsonProcessor = new JacksonJsonProcessor(new ObjectMapper());

        String json = """
            {
              "name": "Pikachu",
              "level": 25,
              "enabled": true,
              "nullable": null
            }
            """;

        Assertions.assertEquals("Pikachu", jsonProcessor.extractValue(json, "/name"));
        Assertions.assertEquals(25, jsonProcessor.extractValue(json, "/level"));
        Assertions.assertEquals(true, jsonProcessor.extractValue(json, "/enabled"));
        Assertions.assertNull(jsonProcessor.extractValue(json, "/nullable"));
    }

    @Test
    void shouldWrapJsonProcessingExceptionAsJsonSerializationException()
            throws JsonProcessingException {

        objectMapper = Mockito.mock(ObjectMapper.class);
        jsonProcessor = new JacksonJsonProcessor(objectMapper);

        JsonProcessingException cause =
                Mockito.mock(JsonProcessingException.class);

        Mockito
                .when(objectMapper.writeValueAsString(Mockito.anyString()))
                .thenThrow(cause);

        JsonSerializationException exception = assertThrows(
                JsonSerializationException.class,
                () -> jsonProcessor.serialize(Mockito.anyString())
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
        jsonProcessor = new JacksonJsonProcessor(objectMapper);

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
                () -> jsonProcessor.extractValue(
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
    void shouldDeserializeJsonString() {

        objectMapper = new ObjectMapper();
        jsonProcessor = new JacksonJsonProcessor(objectMapper);

        AzureDevOpsConfig expected = new AzureDevOpsConfig(
                "azure",
                "my-project",
                "impediment",
                "1234567890"
        );

        AzureDevOpsConfig result = jsonProcessor.deserialize(
                """
                {
                  "organization": "azure",
                  "project": "my-project",
                  "workItemType": "impediment",
                  "pat": "1234567890"
                }
                """,
                AzureDevOpsConfig.class
        );

        Assertions.assertEquals(expected, result);
    }

    @Test
    void shouldWrapJsonProcessingExceptionAsJsonDeserializationException()
            throws JsonProcessingException {

        objectMapper = Mockito.mock(ObjectMapper.class);
        jsonProcessor = new JacksonJsonProcessor(objectMapper);

        JsonProcessingException cause =
                Mockito.mock(JsonProcessingException.class);

        String json = "{\"username\":\"user\",\"password\":\"password\"}";

        record TestCredentials(
                String username,
                String password
        ) {}

        Mockito
                .when(objectMapper.readValue(
                        json,
                        TestCredentials.class
                ))
                .thenThrow(cause);

        JsonDeserializationException exception = assertThrows(
                JsonDeserializationException.class,
                () -> jsonProcessor.deserialize(
                        json,
                        TestCredentials.class
                )
        );

        Assertions.assertSame(cause, exception.getCause());

        Assertions.assertEquals(
                "Could not deserialize data from JSON",
                exception.getMessage()
        );

        Mockito.verify(objectMapper)
                .readValue(json, TestCredentials.class);
    }

    @Test
    void shouldThrowJsonExtractionExceptionIfJsonNodeIsMissing() {

        objectMapper = new ObjectMapper();
        jsonProcessor = new JacksonJsonProcessor(objectMapper);

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
                () -> jsonProcessor.extractValue(json, missingPath)
        );

        Assertions.assertEquals(
                "JSON path does not exist: " + missingPath,
                exception.getMessage()
        );
    }
}
