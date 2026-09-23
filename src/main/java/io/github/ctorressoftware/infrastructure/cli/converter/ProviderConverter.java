package io.github.ctorressoftware.infrastructure.cli.converter;

import io.github.ctorressoftware.application.port.in.provider.configure.Provider;
import picocli.CommandLine;

import java.util.Arrays;
import java.util.Locale;

public class ProviderConverter implements CommandLine.ITypeConverter<Provider> {

    @Override
    public Provider convert(String value) {
        return Arrays.stream(Provider.values())
                .filter(provider ->
                        provider.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() ->
                        new CommandLine.TypeConversionException(
                                "Unsupported provider '" + value +
                                        "'. Supported providers: " +
                                        Arrays.stream(Provider.values())
                                                .map(provider -> provider.name().toLowerCase(Locale.ROOT))
                                                .collect(java.util.stream.Collectors.joining(", "))
                        )
                );
    }
}
