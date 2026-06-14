package io.github.ctorressoftware.infrastructure.exporter;

import java.net.http.HttpRequest;
import java.util.List;
import java.util.Map;

public class CurlGenerator {

    public static String toCurl(HttpRequest request, String bodyPayload) {
        StringBuilder curl = new StringBuilder("curl");
        curl.append(" -X ").append(request.method());

        Map<String, List<String>> headers = request.headers().map();
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String headerName = entry.getKey();
            for (String headerValue : entry.getValue()) {
                curl.append(" -H \"").append(headerName).append(": ").append(headerValue).append("\"");
            }
        }

        if (bodyPayload != null && !bodyPayload.isEmpty()) {
            String escapedBody = bodyPayload.replace("\"", "\\\"");
            curl.append(" -d \"").append(escapedBody).append("\"");
        }

        curl.append(" \"").append(request.uri().toString()).append("\"");

        return curl.toString();
    }
}
