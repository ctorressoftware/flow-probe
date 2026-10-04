# Architecture

FlowProbe uses a hexagonal architecture with explicit dependency wiring.

It does not use a dependency-injection framework. `AppConfig` acts as the composition root and connects the application ports to infrastructure adapters through constructors.

## Package layout

```text
io.github.ctorressoftware
├── domain
├── application
│   ├── port
│   │   ├── in
│   │   └── out
│   └── usecase
└── infrastructure
    ├── callservice
    ├── cli
    ├── json
    ├── persistence
    ├── provider
    ├── readfile
    ├── renderer
    └── ticket
```

## Domain

The domain contains the core flow concepts and domain-level exceptions.

Examples include:

- `Flow`
- `FlowStep`
- `ServiceCall`
- `ExpectedResponse`
- `BodyExpectation`
- `Context`
- `ContextVariable`
- `FlowExecutionSummary`
- `ResponseValidationResult`
- `ReproducibleRequest`

The domain does not depend on Picocli, SnakeYAML, Jackson, Java HTTP Client, or Azure DevOps infrastructure.

## Application

The application layer exposes inbound and outbound ports and coordinates use cases.

Inbound use cases include:

- reading a flow;
- executing a flow;
- configuring a provider;
- creating an impediment ticket.

Outbound ports include abstractions for:

- HTTP/service calls;
- JSON processing;
- flow file reading;
- provider configuration persistence;
- credential storage;
- ticket creation;
- provider prompting;
- request rendering.

The application layer also contains flow execution behavior such as:

- placeholder resolution;
- context management;
- response validation;
- expectation evaluator selection.

## Infrastructure

Infrastructure contains adapters for external technologies and user interfaces.

### CLI

Picocli commands and CLI adapters live under:

```text
infrastructure/cli
```

This includes the root command, `run`, `configure`, provider conversion, version provider, and CLI-specific exit handling.

### YAML

SnakeYAML-backed flow loading lives under:

```text
infrastructure/readfile/yaml
```

YAML DTOs are validated and mapped into domain models before execution.

### HTTP

Java HTTP Client integration lives under:

```text
infrastructure/callservice
```

`RequestMapper` turns a domain `ServiceCall` into an `HttpRequest`.

### JSON

Jackson is behind the application `JsonProcessor` port.

### Persistence

Provider configuration is stored through a repository adapter backed by the operating-system keystore integration.

### Ticketing

Azure DevOps adapters implement the ticket creation ports and REST interaction.

### Rendering

`CurlRequestRenderer` converts executed requests into reproducible cURL commands and redacts common sensitive headers.

## Composition root

`AppConfig` explicitly constructs:

- the Jackson `ObjectMapper`;
- JSON processor;
- YAML reader;
- HTTP client and request mapper;
- response validators and evaluators;
- flow executor;
- keystore-backed provider configuration;
- Azure DevOps ticket integration;
- provider prompt/configuration handlers.

`FlowProbeApplication` then wires the CLI commands to those use cases.

## Execution path

The high-level `run` path is:

```text
Picocli RunCommand
      ↓
ReadFileUseCase
      ↓
YAML reader / validator / mapper
      ↓
ExecuteFlowUseCase
      ↓
FlowExecutor
      ↓
placeholder resolution
      ↓
HTTP service call
      ↓
response validation
      ↓
context exports
      ↓
execution summary
      ↓
CLI rendering
```

If `--create-impediment` is enabled and the flow fails, the execution summary is converted into an impediment ticket request through the ticket creation use case.

## Design intent

The current boundaries make it possible to change infrastructure details without pushing those dependencies into the domain model.

Examples include replacing the YAML reader, JSON processor, HTTP caller, credential storage, request renderer, or ticket provider behind their respective ports.
