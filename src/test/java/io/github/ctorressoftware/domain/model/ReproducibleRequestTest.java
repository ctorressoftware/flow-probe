package io.github.ctorressoftware.domain.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

class ReproducibleRequestTest {

    @Test
    void shouldCreateReproducibleRequestFromServiceCall() {

        ServiceCall serviceCall = new ServiceCall(
                "https://pokeapi.co",
                "GET",
                Map.of("accept", "application/json"),
                null
        );

        ReproducibleRequest request = ReproducibleRequest.fromServiceCall(serviceCall);
        Assertions.assertEquals(serviceCall.url(), request.url());
        Assertions.assertEquals(serviceCall.method(), request.method());
        Assertions.assertEquals(serviceCall.headers(), request.headers());
        Assertions.assertEquals(serviceCall.body(), request.body());
    }

    @Test
    void shouldThrowWhenServiceCallIsNull() {

        IllegalArgumentException exception =
                Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> ReproducibleRequest.fromServiceCall(null)
                );

        Assertions.assertEquals(
                "serviceCall cannot be null",
                exception.getMessage()
        );
    }
}
