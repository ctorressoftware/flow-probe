package io.github.ctorressoftware.application.port.out;

import io.github.ctorressoftware.domain.model.ServiceCall;

public interface ServiceCaller {
    void call(ServiceCall serviceCall);
}
