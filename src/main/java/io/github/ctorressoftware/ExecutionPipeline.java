package io.github.ctorressoftware;

import io.github.ctorressoftware.exceptions.NoDefinedStepsException;
import io.github.ctorressoftware.model.ExecutionContext;
import io.github.ctorressoftware.model.Proxy;
import io.github.ctorressoftware.model.RequestFormat;
import io.github.ctorressoftware.model.StepFormat;

import java.util.List;

public class ExecutionPipeline {

    private final Proxy proxy;
    private final ExecutionContext context;

    public ExecutionPipeline() {
        this.proxy = new Proxy();
        this.context = new ExecutionContext();
    }

    public void execute(List<StepFormat> steps) {

        if (steps == null || steps.isEmpty()) {
            throw new NoDefinedStepsException();
        }

        steps.forEach(step -> {

            step.expect(); // Validate expectations

            proxy.call(new RequestFormat(
                    step.request().url(),
                    step.request().method(),
                    step.request().headers(),
                    step.request().body()
            ));

            step.export(); // Export results to context

        });
    }

    private String resolvePlaceholder() {

        return "";
    }
}
