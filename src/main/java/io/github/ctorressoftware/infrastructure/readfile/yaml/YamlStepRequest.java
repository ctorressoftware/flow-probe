package io.github.ctorressoftware.infrastructure.readfile.yaml;

import java.util.Map;
import java.util.Objects;

public class YamlStepRequest {
    private String url;
    private String method;
    private Map<String, String> headers;
    private Object body;

    public YamlStepRequest() {}

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}