package io.github.ctorressoftware.infrastructure.readfile.yaml;

public class YamlBodyExpectation {
    private String path;
    private String operator;
    private Object value;

    public YamlBodyExpectation() {}

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
