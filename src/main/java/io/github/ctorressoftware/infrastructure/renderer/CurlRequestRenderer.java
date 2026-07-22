package io.github.ctorressoftware.infrastructure.renderer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ctorressoftware.application.port.out.RequestRenderer;
import io.github.ctorressoftware.domain.model.ReproducibleRequest;
import io.github.ctorressoftware.domain.model.RequestFormat;

import java.util.Map;

public class CurlRequestRenderer implements RequestRenderer {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String render(ReproducibleRequest request) {
        StringBuilder curl = new StringBuilder("curl");

        curl.append(" -X ").append(request.method());
        appendHeaders(curl, request.headers());
        appendBody(curl, request.body());
        curl.append(" ").append(singleQuote(request.url()));

        return curl.toString();
    }

    @Override
    public boolean supports(RequestFormat format) {
        return format == RequestFormat.CURL;
    }

    private void appendHeaders(StringBuilder curl, Map<String, String> headers) {
        if (headers == null || headers.isEmpty()) return;

        headers.forEach((name, value) -> curl
                .append(" -H ")
                .append(singleQuote(name + ": " + value))
        );
    }

    private void appendBody(StringBuilder curl, Object body) {
        if (body == null) return;

        try {
            String bodyString = mapper.writeValueAsString(body);
            curl.append(" -d ").append(singleQuote(bodyString));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e); // TODO: create a custom exception
        }
    }

    private String singleQuote(String value) {
        return "'" + value.replace("'", "'\"'\"'") + "'";
    }
}
