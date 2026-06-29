package io.github.ctorressoftware.application.port.out;

import io.github.ctorressoftware.domain.model.ReproducibleRequest;
import io.github.ctorressoftware.domain.model.RequestFormat;

public interface RequestRenderer {
    String render(ReproducibleRequest request);
    boolean supports(RequestFormat format);
}
