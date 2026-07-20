package io.github.ctorressoftware.infrastructure.readfile.yaml;

import java.util.List;
import java.util.Objects;

public class YamlFlow {
    private String name;
    private List<YamlStep> steps;

    public YamlFlow() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<YamlStep> getSteps() {
        return steps;
    }

    public void setSteps(List<YamlStep> steps) {
        this.steps = steps;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof YamlFlow yamlFlow)) return false;
        return Objects.equals(name, yamlFlow.name) && Objects.equals(steps, yamlFlow.steps);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, steps);
    }

    @Override
    public String toString() {
        return "YamlFlow{" +
                "name='" + name + '\'' +
                ", steps=" + steps +
                '}';
    }
}