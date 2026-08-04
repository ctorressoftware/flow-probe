package io.github.ctorressoftware.application.usecase.flowexecution;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
class PlaceholderResolverTest {

    @Test
    void shouldResolveUrlPlaceholdersCorrectly() {

        String url = "https://placeholder-resolver.com/${variableToResolve}";

        Map<String, Object> variables = Map.of("variableToResolve", "resolved");

        String normalizeUrl = PlaceholderResolver.resolve(variables, url);

        Assertions.assertEquals(
                "https://placeholder-resolver.com/resolved",
                normalizeUrl
        );
    }


}
