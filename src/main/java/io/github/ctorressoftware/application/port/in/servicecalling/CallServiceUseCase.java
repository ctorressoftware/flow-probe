package io.github.ctorressoftware.application.port.in.servicecalling;

public interface CallServiceUseCase {
    CallServiceResult call(CallServiceCommand command);
}
