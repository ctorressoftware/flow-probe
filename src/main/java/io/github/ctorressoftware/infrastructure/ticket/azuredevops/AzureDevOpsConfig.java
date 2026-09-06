package io.github.ctorressoftware.infrastructure.ticket.azuredevops;

import io.github.ctorressoftware.application.port.in.provider.configure.Provider;
import io.github.ctorressoftware.application.port.out.ProviderConfig;

public record AzureDevOpsConfig(
        String organization,
        String project,
        String workItemType,
        String pat
) implements ProviderConfig {

    @Override
    public Provider provider() {
        return Provider.AZURE;
    }
}