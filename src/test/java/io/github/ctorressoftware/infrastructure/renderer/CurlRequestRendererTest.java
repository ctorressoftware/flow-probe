package io.github.ctorressoftware.infrastructure.renderer;

import io.github.ctorressoftware.application.exception.JsonSerializationException;
import io.github.ctorressoftware.application.port.out.JsonProcessor;
import io.github.ctorressoftware.domain.constant.HttpMethod;
import io.github.ctorressoftware.domain.model.ReproducibleRequest;
import io.github.ctorressoftware.domain.model.RequestFormat;
import io.github.ctorressoftware.infrastructure.renderer.exception.InvalidCurlBodyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CurlRequestRendererTest {

    @Mock
    private JsonProcessor jsonProcessor;

    private CurlRequestRenderer renderer;

    @BeforeEach
    void init() {
        this.renderer = new CurlRequestRenderer(jsonProcessor);
    }

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
    void shouldGetOutOfAppendHeadersMethodIfHeadersAreEmpty() {

        ReproducibleRequest request = new ReproducibleRequest(
                "https://pokeapi.co/api/v2/pokemon?offset=0&limit=1350",
                HttpMethod.GET,
                Map.of(),
                null
        );

        String expected = "curl -X GET " +
                "'https://pokeapi.co/api/v2/pokemon?offset=0&limit=1350'";

        String curl = renderer.render(request);
        assertEquals(expected, curl);
    }

    @Test
    void shouldGetOutOfAppendHeadersMethodIfHeadersAreNull() {

        ReproducibleRequest request = new ReproducibleRequest(
                "https://pokeapi.co/api/v2/pokemon?offset=0&limit=1350",
                HttpMethod.GET,
                null,
                null
        );

        String expected = "curl -X GET " +
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

        String expected = "curl -X POST " +
                "-H 'Content-Type: application/json' " +
                "-H 'Accept: application/json' " +
                "-d '{\"option\":\"1\",\"topic\":\"example\"}' " +
                "'https://example.co/api/v1/post-example'";

        Mockito.when(jsonProcessor.serialize(request.body()))
                .thenReturn( "{\"option\":\"1\",\"topic\":\"example\"}");

        String curl = renderer.render(request);
        
        assertEquals(expected, curl);
    }

    @Test
    void shouldWrapJsonSerializationExceptionAsInvalidCurlBodyException() {

        Object body = new Object();

        ReproducibleRequest request = new ReproducibleRequest(
                "https://example.co/api/v1/post-example",
                HttpMethod.POST,
                Map.of(),
                body
        );

        JsonSerializationException cause =
                Mockito.mock(JsonSerializationException.class);

        Mockito.when(jsonProcessor.serialize(body))
                .thenThrow(cause);

        InvalidCurlBodyException exception = assertThrows(
                InvalidCurlBodyException.class,
                () -> renderer.render(request)
        );

        assertSame(cause, exception.getCause());

        Mockito.verify(jsonProcessor)
                .serialize(body);
    }

    @Test
    void shouldReturnTrueIfRequestFormatIsCurl() {
        assertTrue(renderer.supports(RequestFormat.CURL));
    }

    @Test
    void shouldReturnFalseIfRequestFormatIsNotCurl() {
        assertFalse(renderer.supports(RequestFormat.HTTP_RAW));
    }
}
