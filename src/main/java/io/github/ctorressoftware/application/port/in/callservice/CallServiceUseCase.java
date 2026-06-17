package io.github.ctorressoftware.application.port.in.callservice;

public interface CallServiceUseCase {
    CallServiceResult call(CallServiceCommand command);
}
