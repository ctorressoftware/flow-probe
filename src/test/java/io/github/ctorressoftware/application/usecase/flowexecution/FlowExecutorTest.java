package io.github.ctorressoftware.application.usecase.flowexecution;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ctorressoftware.application.port.out.ServiceCaller;
import io.github.ctorressoftware.domain.model.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FlowExecutorTest {

    @Mock
    private Context context;

    @Mock
    private ServiceCaller serviceCaller;

    @Mock
    private ObjectMapper objectMapper;

    private FlowExecutor flowExecutor;

    @BeforeEach
    void init() {
        flowExecutor = new FlowExecutor(context, serviceCaller, objectMapper);
    }

    @Test
    void shouldExecuteFlowCorrectly() {

    }
}
