package io.github.ctorressoftware.application.usecase.flowexecution;

import io.github.ctorressoftware.domain.model.ContextVariable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class PlaceholderResolverTest {

    @Test
    void shouldResolveUrlPlaceholdersCorrectly() {

        String url = "https://placeholder-resolver.com/${variableToResolve}";

        List<ContextVariable> variables = List.of(new ContextVariable("variableToResolve", "variableValue"));

        String normalizeUrl = PlaceholderResolver.resolve(variables, url);

        Assertions.assertEquals(
                "https://placeholder-resolver.com/variableValue",
                normalizeUrl
        );
    }

    @Test
    void shouldReturnsNullWhenValueIsNull() {

        List<ContextVariable> variables = List.of(new ContextVariable("variableName", "variableValue"));

        Assertions.assertNull(
                PlaceholderResolver.resolve(variables, null)
        );
    }

}
