package io.github.ctorressoftware.application.port.out;

import io.github.ctorressoftware.domain.model.Flow;
import io.github.ctorressoftware.domain.model.FlowExecutionSummary;

public interface Executor {
    /*
     * TODO: Evaluate whether to introduce a common execution summary interface
     *       and support multiple flow execution.
     *       This could enable implementations such as MultipleFlowExecutor
     *       and FolderFlowExecutor.
     */
    FlowExecutionSummary execute(Flow flow);
}