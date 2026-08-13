package io.github.ctorressoftware.infrastructure.json.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JacksonJsonProcessorTest {

    private ObjectMapper objectMapper;

    private JacksonJsonProcessor jacksonJsonProcessor;

    @BeforeEach
    void init() {
        objectMapper = new ObjectMapper();
        jacksonJsonProcessor = new JacksonJsonProcessor(objectMapper);
    }

    @Test
    void shouldReturnSerializedText() {

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

}
