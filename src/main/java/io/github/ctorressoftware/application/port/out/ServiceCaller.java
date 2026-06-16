package io.github.ctorressoftware.application.port.out;

import io.github.ctorressoftware.domain.model.ServiceCall;
import io.github.ctorressoftware.domain.model.CallResult;

public interface ServiceCaller {
    CallResult call(ServiceCall serviceCall);
}
