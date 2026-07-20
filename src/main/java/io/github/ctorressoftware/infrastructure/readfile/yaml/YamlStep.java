package io.github.ctorressoftware.infrastructure.readfile.yaml;

import java.util.Map;
import java.util.Objects;

public class YamlStep {
    private String name;
    private YamlStepRequest request;
    private Map<String, String> requires;
    private Map<String, String> exports;

    public YamlStep() {
    }

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

    public Map<String, String> getRequires() {
        return requires;
    }

    public void setRequires(Map<String, String> requires) {
        this.requires = requires;
    }

    public Map<String, String> getExports() {
        return exports;
    }

    public void setExports(Map<String, String> exports) {
        this.exports = exports;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof YamlStep yamlStep)) return false;
        return Objects.equals(name, yamlStep.name) && Objects.equals(request, yamlStep.request) && Objects.equals(requires, yamlStep.requires) && Objects.equals(exports, yamlStep.exports);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, request, requires, exports);
    }

    @Override
    public String toString() {
        return "YamlStep{" +
                "name='" + name + '\'' +
                ", request=" + request +
                ", requires=" + requires +
                ", exports=" + exports +
                '}';
    }
}