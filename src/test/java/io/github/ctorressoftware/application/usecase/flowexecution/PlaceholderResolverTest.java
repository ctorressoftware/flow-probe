package io.github.ctorressoftware.application.usecase.flowexecution;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ctorressoftware.domain.model.ContextVariable;
import io.github.ctorressoftware.domain.model.ServiceCall;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class PlaceholderResolverTest {

    private final JsonUtils jsonUtils = new JsonUtils(new ObjectMapper());

    private final PlaceholderResolver placeholderResolver;

    public PlaceholderResolverTest() {
        placeholderResolver = new PlaceholderResolver(jsonUtils);
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
    void shouldReturnsNullWhenServiceCallIsNull() {

        List<ContextVariable> variables = List.of(new ContextVariable("variableName", "variableValue"));

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> placeholderResolver.resolve(variables, null)
        );
    }

}
