package io.github.ctorressoftware.infrastructure.callservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ctorressoftware.application.exception.JsonSerializationException;
import io.github.ctorressoftware.application.port.out.JsonProcessor;
import io.github.ctorressoftware.domain.constant.HttpMethod;
import io.github.ctorressoftware.domain.exception.HttpServiceCallException;
import io.github.ctorressoftware.domain.model.ServiceCall;
import io.github.ctorressoftware.infrastructure.json.jackson.JacksonJsonProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class RequestMapperTest {

    private JsonProcessor jsonProcessor;
    private RequestMapper requestMapper;

    @Test
    void shouldReturnHttpRequestForServiceCallWithGetMethod() {

        jsonProcessor = new JacksonJsonProcessor(new ObjectMapper());
        requestMapper = new RequestMapper(jsonProcessor);

        ServiceCall serviceCall = new ServiceCall(
                "https://pokeapi.co/api/v2/pokemon?offset=0&limit=1350",
                HttpMethod.GET,
                Map.of("Content-Type", "Content-Type"),
                null
        );

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serviceCall.url()))
                .headers("Content-Type", "Content-Type")
                .method(serviceCall.method(), HttpRequest.BodyPublishers.noBody())
                .build();

        Assertions.assertEquals(
                httpRequest,
                requestMapper.map(serviceCall)
        );
    }

    @Test
    void shouldWrapJsonProcessingExceptionAsHttpServiceCallException() {

        jsonProcessor = Mockito.mock(JacksonJsonProcessor.class);
        requestMapper = new RequestMapper(jsonProcessor);

        ServiceCall request = new ServiceCall(
                "https://pokeapi.co/api/v2/pokemon?offset=0&limit=1350",
                HttpMethod.POST,
                Map.of("headerTest", "testValue"),
                Map.of("bodyTest", "testValue")
        );

        JsonSerializationException cause =
                Mockito.mock(JsonSerializationException.class);

        Mockito
                .when(jsonProcessor.serialize(request.body()))
                .thenThrow(cause);

        HttpServiceCallException exception = assertThrows(
                HttpServiceCallException.class,
                () -> requestMapper.map(request)
        );

        Assertions.assertEquals("Could not serialize request body to JSON", exception.getMessage());
        Mockito.verify(jsonProcessor, Mockito.times(1)).serialize(Mockito.any());
        Mockito.verifyNoMoreInteractions(jsonProcessor);

    }
}
