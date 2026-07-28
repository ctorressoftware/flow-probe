package io.github.ctorressoftware.infrastructure.callservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ctorressoftware.domain.constant.HttpMethod;
import io.github.ctorressoftware.domain.exception.HttpServiceCallException;
import io.github.ctorressoftware.domain.model.ServiceCall;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class RequestMapperTest {

    @Mock
    private ObjectMapper objectMapper;

    private RequestMapper requestMapper;

    @BeforeEach
    void init() {
        this.requestMapper = new RequestMapper(objectMapper);
    }

    @Test
    void shouldWrapJsonProcessingExceptionAsHttpServiceCallException()
            throws JsonProcessingException {

        ServiceCall request = new ServiceCall(
                "https://pokeapi.co/api/v2/pokemon?offset=0&limit=1350",
                HttpMethod.POST,
                Map.of("headerTest", "testValue"),
                Map.of("bodyTest", "testValue")
        );

        JsonProcessingException cause =
                Mockito.mock(JsonProcessingException.class);

        Mockito
                .when(objectMapper.writeValueAsString(request.body()))
                .thenThrow(cause);

        HttpServiceCallException exception = assertThrows(
                HttpServiceCallException.class,
                () -> requestMapper.map(request)
        );

        assertEquals(
                "Could not serialize request body to JSON",
                exception.getMessage()
        );
    }

}
