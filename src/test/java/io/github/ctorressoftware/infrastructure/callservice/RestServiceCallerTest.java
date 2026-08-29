package io.github.ctorressoftware.infrastructure.callservice;

import io.github.ctorressoftware.domain.constant.HttpMethod;
import io.github.ctorressoftware.domain.exception.HttpServiceCallException;
import io.github.ctorressoftware.domain.model.ServiceCall;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RestServiceCallerTest {

    @Mock
    private HttpClient client;

    @Mock
    private RequestMapper requestMapper;

    private RestServiceCaller restServiceCaller;

    @BeforeEach
    void init() {
        this.restServiceCaller = new RestServiceCaller(client, requestMapper);
    }

    @Test
    void shouldWrapIOExceptionAsHttpServiceCallException()
            throws IOException, InterruptedException {

        ServiceCall serviceCall = new ServiceCall(
                "https://pokeapi.co/api/v2/pokemon?offset=0&limit=1350",
                HttpMethod.GET,
                Map.of(),
                null
        );

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serviceCall.url()))
                .GET()
                .build();

        Mockito.when(requestMapper.map(serviceCall))
                .thenReturn(httpRequest);

        IOException cause =
                new IOException("Simulated connection failure");

        Mockito.when(client.send(
                        Mockito.same(httpRequest),
                        Mockito.<HttpResponse.BodyHandler<String>>any()
                ))
                .thenThrow(cause);

        HttpServiceCallException exception = assertThrows(
                HttpServiceCallException.class,
                () -> restServiceCaller.call(serviceCall)
        );

        assertSame(cause, exception.getCause());

        Mockito.verify(requestMapper)
                .map(serviceCall);

        Mockito.verify(client)
                .send(
                        Mockito.same(httpRequest),
                        Mockito.<HttpResponse.BodyHandler<String>>any()
                );
    }
}
