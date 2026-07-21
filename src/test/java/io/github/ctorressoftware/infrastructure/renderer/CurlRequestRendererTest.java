package io.github.ctorressoftware.infrastructure.renderer;

import io.github.ctorressoftware.domain.constant.HttpMethod;
import io.github.ctorressoftware.domain.model.ReproducibleRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

public class CurlRequestRendererTest {

    private final CurlRequestRenderer renderer = new CurlRequestRenderer();

    @Test
    void renderCurlWithGetMethod() {

        ReproducibleRequest reproducibleRequest = new ReproducibleRequest(
                "https://pokeapi.co/api/v2/pokemon?offset=0&limit=1350",
                HttpMethod.GET,
                Map.of("accept", "application/json"),
                null
        );

        String expected = "curl -X GET -H 'accept: application/json' 'https://pokeapi.co/api/v2/pokemon?offset=0&limit=1350'";

        String curl = renderer.render(reproducibleRequest);

        assertEquals(
                expected,
                curl
        );

    }
}
