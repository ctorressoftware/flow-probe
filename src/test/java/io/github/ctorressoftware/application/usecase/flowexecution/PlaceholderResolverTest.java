package io.github.ctorressoftware.application.usecase.flowexecution;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ctorressoftware.application.port.out.JsonProcessor;
import io.github.ctorressoftware.domain.model.ContextVariable;
import io.github.ctorressoftware.infrastructure.json.jackson.JacksonJsonProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class PlaceholderResolverTest {

    private final JsonProcessor jsonProcessor = new JacksonJsonProcessor(new ObjectMapper());

    private final PlaceholderResolver placeholderResolver;

    public PlaceholderResolverTest() {
        placeholderResolver = new PlaceholderResolver(jsonProcessor);
    }

    @Test
    void shouldResolveUrlPlaceholdersCorrectly() {

        String url = "https://placeholder-resolver.com/${variableToResolve}";

        List<ContextVariable> variables = List.of(new ContextVariable("variableToResolve", "variableValue"));

        String normalizeUrl = placeholderResolver.resolveString(variables, url);

        Assertions.assertEquals(
                "https://placeholder-resolver.com/variableValue",
                normalizeUrl
        );
    }

    @Test
    void shouldReturnNullWhenServiceCallIsNull() {

        List<ContextVariable> variables = List.of(new ContextVariable("variableName", "variableValue"));

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> placeholderResolver.resolve(variables, null)
        );
    }

    @Test
    void shouldResolveStringMapCorrectly() {

        Map<String, String> headers = Map.of(
            "accept", "${variableToResolve}"
        );

        Map<String, String> expected = Map.of(
            "accept", "variableValue"
        );

        List<ContextVariable> variables = List.of(new ContextVariable("variableToResolve", "variableValue"));

        Map<String, String> resolved = placeholderResolver.resolveMap(variables, headers);

        Assertions.assertEquals(expected,resolved);
    }
}
