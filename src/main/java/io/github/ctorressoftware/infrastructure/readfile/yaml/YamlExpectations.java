package io.github.ctorressoftware.infrastructure.readfile.yaml;

import java.util.List;

public class YamlExpectations {

    private Integer status;
    private List<YamlBodyExpectation> body;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<YamlBodyExpectation> getBody() {
        return body;
    }

    public void setBody(List<YamlBodyExpectation> body) {
        this.body = body;
    }
}
