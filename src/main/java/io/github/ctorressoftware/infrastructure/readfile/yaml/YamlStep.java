package io.github.ctorressoftware.infrastructure.readfile.yaml;

import java.util.Map;
import java.util.Objects;

public class YamlStep {
    private String name;
    private YamlStepRequest request;
    private YamlExpectations expect;
    private Map<String, String> exports;

    public YamlStep() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public YamlStepRequest getRequest() {
        return request;
    }

    public void setRequest(YamlStepRequest request) {
        this.request = request;
    }

    public YamlExpectations getExpect() {
        return expect;
    }

    public void setExpect(YamlExpectations expect) {
        this.expect = expect;
    }

    public Map<String, String> getExports() {
        return exports;
    }

    public void setExports(Map<String, String> exports) {
        this.exports = exports;
    }
}