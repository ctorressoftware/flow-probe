package io.github.ctorressoftware.application.usecase.flowexecution;

import io.github.ctorressoftware.domain.model.ContextVariable;
import io.github.ctorressoftware.domain.model.ServiceCall;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class PlaceholderResolverTest {

    private final PlaceholderResolver placeholderResolver = new PlaceholderResolver();

    @Test
    void shouldResolveServiceCallPlaceholders() {

        List<ContextVariable> variables = List.of(
                new ContextVariable("host", "pokeapi.co"),
                new ContextVariable("pokemon", "Pikachu"),
                new ContextVariable("method", "GET"),
                new ContextVariable("token", "123456")
        );

        ServiceCall serviceCall = new ServiceCall(
                "https://${host}/api/v2/pokemon/${pokemon}",
                "${method}",
                Map.of(
                        "Authorization", "Bearer ${token}",
                        "X-Pokemon", "${pokemon}"
                ),
                null
        );

        ServiceCall resolved = placeholderResolver.resolve(variables, serviceCall);

        Assertions.assertEquals(
                "https://pokeapi.co/api/v2/pokemon/Pikachu",
                resolved.url()
        );

        Assertions.assertEquals("GET", resolved.method());

        Assertions.assertEquals(
                Map.of(
                        "Authorization", "Bearer 123456",
                        "X-Pokemon", "Pikachu"
                ),
                resolved.headers()
        );

        Assertions.assertNull(resolved.body());
    }

    @Test
    void shouldResolveBodyPlaceholders() {

        List<ContextVariable> variables = List.of(
                new ContextVariable("pokemonName", "Pikachu"),
                new ContextVariable("level", 25)
        );

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("name", "${pokemonName}");
        body.put("level", "${level}");

        ServiceCall serviceCall = new ServiceCall(
                "https://pokeapi.co",
                "POST",
                Map.of("Content-Type", "application/json"),
                body
        );

        ServiceCall resolved = placeholderResolver.resolve(variables, serviceCall);

        Map<String, Object> expected = new LinkedHashMap<>();
        expected.put("name", "Pikachu");
        expected.put("level", 25);
        Assertions.assertEquals(expected, resolved.body());
    }

    @Test
    void shouldPreserveNullBody() {

        ServiceCall serviceCall = new ServiceCall(
                "https://pokeapi.co",
                "GET",
                null,
                null
        );

        ServiceCall resolved = placeholderResolver.resolve(List.of(), serviceCall);

        Assertions.assertNull(resolved.body());
    }

    @Test
    void shouldResolveServiceCallWithNullOptionalValues() {

        ServiceCall serviceCall =
                new ServiceCall(null, null, null, null);

        ServiceCall resolved = placeholderResolver.resolve(List.of(), serviceCall);

        Assertions.assertNull(resolved.url());
        Assertions.assertNull(resolved.method());
        Assertions.assertTrue(resolved.headers().isEmpty());
        Assertions.assertNull(resolved.body());
    }

    @Test
    void shouldLeaveServiceCallUnchangedWhenThereAreNoVariables() {

        ServiceCall serviceCall = new ServiceCall(
                "https://pokeapi.co/api/v2/pokemon",
                "GET",
                Map.of("accept", "application/json"),
                null
        );

        ServiceCall resolved = placeholderResolver.resolve(List.of(), serviceCall);

        Assertions.assertEquals(serviceCall, resolved);
    }

    @Test
    void shouldThrowWhenServiceCallIsNull() {

        IllegalArgumentException exception =
                Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> placeholderResolver.resolve(List.of(), null)
                );

        Assertions.assertEquals(
                "serviceCall cannot be null",
                exception.getMessage()
        );
    }

    @Test
    void shouldReplaceNullVariableValueWithEmptyString() {

        List<ContextVariable> variables = List.of(
                new ContextVariable("pokemonName", null)
        );

        ServiceCall serviceCall = new ServiceCall(
                "https://pokeapi.co/api/v2/pokemon/${pokemonName}",
                "GET",
                Map.of("X-Pokemon", "${pokemonName}"),
                null
        );

        ServiceCall resolved = placeholderResolver.resolve(variables, serviceCall);

        Assertions.assertEquals(
                "https://pokeapi.co/api/v2/pokemon/null",
                resolved.url()
        );

        Assertions.assertEquals(
                Map.of("X-Pokemon", "null"),
                resolved.headers()
        );

        Assertions.assertNull(resolved.body());
    }

    @Test
    void shouldPreserveNullTypeWhenBodyValueIsExactPlaceholder() {

        List<ContextVariable> variables = List.of(
                new ContextVariable("pokemonName", null)
        );

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("name", "${pokemonName}");

        ServiceCall serviceCall = new ServiceCall(
                "https://pokeapi.co",
                "POST",
                Map.of(),
                body
        );

        ServiceCall resolved = placeholderResolver.resolve(variables, serviceCall);

        Map<?, ?> resolvedBody = (Map<?, ?>) resolved.body();

        Assertions.assertTrue(resolvedBody.containsKey("name"));
        Assertions.assertNull(resolvedBody.get("name"));
    }

    @Test
    void shouldThrowWhenHeaderKeyIsNull() {

        Map<String, String> headers = new HashMap<>();
        headers.put(null, "value");

        ServiceCall serviceCall = new ServiceCall(
                "https://pokeapi.co",
                "GET",
                headers,
                null
        );

        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> placeholderResolver.resolve(List.of(), serviceCall)
        );

        Assertions.assertEquals(
                "Header key cannot be null",
                exception.getMessage()
        );
    }

    @Test
    void shouldThrowWhenHeaderValueIsNull() {

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", null);

        ServiceCall serviceCall = new ServiceCall(
                "https://pokeapi.co",
                "GET",
                headers,
                null
        );

        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> placeholderResolver.resolve(List.of(), serviceCall)
        );

        Assertions.assertEquals(
                "Header value cannot be null: Authorization",
                exception.getMessage()
        );
    }
}
