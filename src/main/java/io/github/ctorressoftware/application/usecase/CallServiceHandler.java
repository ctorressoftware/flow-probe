package io.github.ctorressoftware.application.usecase;

import io.github.ctorressoftware.application.port.in.callservice.CallServiceCommand;
import io.github.ctorressoftware.application.port.in.callservice.CallServiceResult;
import io.github.ctorressoftware.application.port.in.callservice.CallServiceUseCase;
import io.github.ctorressoftware.application.port.out.ServiceCaller;
import io.github.ctorressoftware.domain.model.CallResult;

public class CallServiceHandler implements CallServiceUseCase {

    private final ServiceCaller serviceCaller;

    public CallServiceHandler(ServiceCaller serviceCaller) {
        this.serviceCaller = serviceCaller;
    }

    @Override
    public CallServiceResult call(CallServiceCommand command) {
        CallResult result = serviceCaller.call(command.serviceCall());
        return new CallServiceResult(result);
    }
}
