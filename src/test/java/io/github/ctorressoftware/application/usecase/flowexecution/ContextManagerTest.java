package io.github.ctorressoftware.application.usecase.flowexecution;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ctorressoftware.application.port.out.JsonProcessor;
import io.github.ctorressoftware.domain.exception.DuplicateVariableException;
import io.github.ctorressoftware.domain.model.Context;
import io.github.ctorressoftware.domain.model.ContextVariable;
import io.github.ctorressoftware.infrastructure.json.jackson.JacksonJsonProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class ContextManagerTest {

    private Context context;
    private JsonProcessor jsonProcessor;
    private ContextManager contextManager;

    @BeforeEach
    void init() {
        this.context = new Context();
        this.jsonProcessor = new JacksonJsonProcessor(new ObjectMapper());
        this.contextManager = new ContextManager(context, jsonProcessor);
    }

    @Test
    void shouldExportContextVariables() {

        String data = """
        {
          "user": {
            "name": "value1",
            "profile": {
              "nickname": "value2"
            }
          },
          "metadata": {
            "code": "value3"
          }
        }
        """;

        Map<String, String> toExport = Map.of(
                "name1", "/user/name",
                "name2", "/user/profile/nickname",
                "name3", "/metadata/code"
        );

        contextManager.exportVariables(data, toExport);

        List<ContextVariable> expected = List.of(
                new ContextVariable("name1", "value1"),
                new ContextVariable("name2", "value2"),
                new ContextVariable("name3", "value3")
        );

        Assertions.assertEquals(
                Set.copyOf(expected),
                Set.copyOf(contextManager.getVariables())
        );
    }

    @Test
    void shouldGetEmptyContextVariables() {
        List<ContextVariable> variables = contextManager.getVariables();
        Assertions.assertTrue(variables.isEmpty());
    }

    @Test
    void shouldRejectDuplicateVariableNamesWhenExporting() {

        String variableName = "variableName";
        String data = """
        {
          "user": {
            "name": "value1"
          }
        }
        """;

        Map<String, String> toExport = Map.of(
                variableName, "/user/name"
        );

        contextManager.exportVariables(data, toExport);

        DuplicateVariableException exception = Assertions.assertThrows(
                DuplicateVariableException.class,
                () -> contextManager.exportVariables(data, toExport)
        );

        Assertions.assertEquals(
                "Variable already exists in context: " + variableName,
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectNullVariableNameWhenExporting() {

        String data = """
            {
              "user": {
                "name": "value1"
              }
            }
            """;

        Map<String, String> toExport = new HashMap<>();
        toExport.put(null, "/user/name");

        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> contextManager.exportVariables(data, toExport)
        );

        Assertions.assertEquals(
                "Variable name cannot be null",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectBlankVariableNameWhenExporting() {

        String data = """
            {
              "user": {
                "name": "value1"
              }
            }
            """;

        Map<String, String> toExport = Map.of(
                " ", "/user/name"
        );

        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> contextManager.exportVariables(data, toExport)
        );

        Assertions.assertEquals(
                "Variable name cannot be blank",
                exception.getMessage()
        );
    }
}
