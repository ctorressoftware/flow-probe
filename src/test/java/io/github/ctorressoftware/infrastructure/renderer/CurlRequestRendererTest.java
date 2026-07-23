package io.github.ctorressoftware.infrastructure.renderer;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.ctorressoftware.domain.constant.HttpMethod;
import io.github.ctorressoftware.domain.model.ReproducibleRequest;
import io.github.ctorressoftware.infrastructure.readfile.exception.EmptyFileException;
import io.github.ctorressoftware.infrastructure.renderer.exception.InvalidCurlBodyException;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CurlRequestRendererTest {

    private final CurlRequestRenderer renderer = new CurlRequestRenderer();

    @Test
    void renderCurlWithGetMethod() {

        ReproducibleRequest request = new ReproducibleRequest(
                "https://pokeapi.co/api/v2/pokemon?offset=0&limit=1350",
                HttpMethod.GET,
                Map.of("accept", "application/json"),
                null
        );

        String expected = "curl -X GET " +
                "-H 'accept: application/json' " +
                "'https://pokeapi.co/api/v2/pokemon?offset=0&limit=1350'";

        String curl = renderer.render(request);
        assertEquals(expected, curl);
    }

    @Test
    void renderCurlWithPostMethod() {

        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");

        Map<String, String> body = new LinkedHashMap<>();
        body.put("option", "1");
        body.put("topic", "example");

        ReproducibleRequest request = new ReproducibleRequest(
                "https://example.co/api/v1/post-example",
                HttpMethod.POST,
                headers,
                body
        );

        String expected = "curl -X POST -H 'Content-Type: application/json' -H 'Accept: application/json' -d '{\"option\":\"1\",\"topic\":\"example\"}' 'https://example.co/api/v1/post-example'";

        String curl = renderer.render(request);
        assertEquals(expected, curl);
    }

    @Test
    void shouldWrapJsonProcessingExceptionAsInvalidCurlBodyException() { // TODO: re-implement with Mockito
        class Person {
            Person friend;
        }

        Person person = new Person();
        person.friend = person;

        ReproducibleRequest request = new ReproducibleRequest(
                "https://example.co/api/v1/post-example",
                HttpMethod.POST,
                Map.of(),
                person
        );

        InvalidCurlBodyException exception = assertThrows(
                InvalidCurlBodyException.class,
                () -> renderer.render(request)
        );

        assertInstanceOf(JsonProcessingException.class, exception.getCause());
    }
}
