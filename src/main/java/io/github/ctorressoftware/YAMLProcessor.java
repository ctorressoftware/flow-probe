package io.github.ctorressoftware;

import io.github.ctorressoftware.model.RequestFormat;
import io.github.ctorressoftware.model.StepFormat;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YAMLProcessor {

    @SuppressWarnings("unchecked")
    public List<StepFormat> read(String filePath) {

        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(filePath);

        Map<String, Object> yamlMap = (Map<String, Object>) yaml.load(inputStream);

        String flowName = yamlMap.get("name").toString();

        List<Map<String, Object>> steps = (List<Map<String, Object>>) yamlMap.get("steps");

        List<StepFormat> formattedSteps = new ArrayList<>();

        for (Map<String, Object> step : steps) {
            String stepName = step.get("name").toString();

            Map<String, Object> request = (Map<String, Object>) step.get("request");
            Map<String, Object> expect = (Map<String, Object>) step.get("expect");
            Map<String, String> export = (Map<String, String>) step.get("export");

            String url = request.get("url").toString();
            String method = request.get("method").toString();
            Map<String, String> headers = (Map<String, String>) request.get("headers");
            Object body = request.get("body");

            RequestFormat requestFormat = resolveRequest(url, method, headers, body);
            StepFormat stepFormat = resolveStep(flowName, stepName, requestFormat, expect, export);
            formattedSteps.add(stepFormat);
        }

        return formattedSteps;
    }

    private RequestFormat resolveRequest(
            String url,
            String method,
            Map<String, String> headers,
            Object body
    ) {
        return new RequestFormat(
                url,
                method,
                headers,
                body
        );
    }

    private StepFormat resolveStep(
            String flowName,
            String stepName,
            RequestFormat requestFormat,
            Map<String, Object> expect,
            Map<String, String> export
    ) {
        return new StepFormat(
                flowName,
                stepName,
                requestFormat,
                expect,
                export
        );
    }
}
