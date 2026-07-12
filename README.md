# FlowProbe

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![GraalVM Native Image](https://img.shields.io/badge/GraalVM-Native%20Image-blue.svg)](https://www.graalvm.org/latest/reference-manual/native-image/)
[![CLI](https://img.shields.io/badge/interface-CLI-lightgrey.svg)](https://picocli.info/)

**FlowProbe** is a command-line tool for defining, executing, and diagnosing HTTP service flows.

A flow is described in YAML as an ordered sequence of HTTP requests. Each step can export values from its response and make them available to subsequent steps through placeholders.

When a flow fails, FlowProbe can generate reproducible cURL commands and optionally create an impediment in Azure DevOps with the relevant failure details.

> FlowProbe is currently under active development. The existing implementation is usable, but its CLI contract, YAML schema, and provider integrations may still change before the first stable release.

---

## Why FlowProbe?

Validating an environment often requires more than checking whether one endpoint returns `200 OK`.

Real workflows usually involve several dependent requests:

1. Call one service.
2. Extract data from its response.
3. Inject that data into another request.
4. Continue until the complete business flow succeeds or fails.
5. Collect enough evidence to reproduce the failure.
6. Report the problem in the team's ticketing platform.

FlowProbe automates that process from a portable YAML definition.

---

## Current features

* Define sequential HTTP flows in YAML.
* Execute HTTP requests in declaration order.
* Export values from JSON responses.
* Resolve placeholders such as `${variableName}` in later steps.
* Render requests as reproducible cURL commands.
* Detect failed flow executions.
* Interactively create impediment tickets in Azure DevOps.
* Configure Azure DevOps credentials from the CLI.
* Store provider credentials in the operating system's native credential store.
* Compile to a standalone native executable with GraalVM Native Image.
* Use a hexagonal architecture with explicit ports, adapters, and manual dependency wiring.

---

## Technology stack

* Java 21
* Gradle Kotlin DSL
* Picocli
* SnakeYAML
* Java HTTP Client
* Jackson
* java-keyring
* GraalVM Native Image
* JUnit 5

---

## Project status

The following commands are currently implemented:

```text
flowprobe configure <provider>
flowprobe run --file <path>
flowprobe --help
flowprobe --version
```

Currently supported provider:

```text
azure
```

Planned commands such as `status`, `remove`, and additional providers are not implemented yet.

---

## Requirements

### JVM execution

* Java 21 or newer

### Native compilation

* GraalVM for JDK 21
* `native-image` available in `PATH`

Verify the installation:

```bash
java --version
native-image --version
```

---

## Build the project

Clone the repository:

```bash
git clone https://github.com/ctorressoftware/flow-probe.git
cd flow-probe
```

Compile the project:

```bash
./gradlew clean build
```

Run the CLI help:

```bash
./gradlew run --args="--help"
```

---

## Usage

### Configure Azure DevOps

Before FlowProbe can create impediments, configure the Azure DevOps provider:

```bash
./gradlew run --args="configure azure"
```

FlowProbe will request:

```text
Azure DevOps organization
Azure DevOps project
Azure DevOps work item type
Azure DevOps Personal Access Token (PAT)
```

The PAT is not written to a plain-text YAML configuration file. It is stored through `java-keyring` in the credential store provided by the operating system.

The macOS Keychain integration has been verified with both regular JVM execution and a GraalVM native executable.

The operating system may request authorization before allowing FlowProbe to access stored credentials.

### Required Azure DevOps PAT permissions

The PAT must have enough permission to create work items in the configured Azure DevOps project.

Use the narrowest permission scope that satisfies that requirement. Giving a CLI administrator access because scopes are annoying would be impressively lazy security.

---

## Run a flow

Using Gradle:

```bash
./gradlew run --args="run --file /absolute/path/to/flow.yaml"
```

Short option:

```bash
./gradlew run --args="run -f /absolute/path/to/flow.yaml"
```

Using the native executable:

```bash
./build/native/nativeCompile/flowprobe run \
  --file /absolute/path/to/flow.yaml
```

FlowProbe renders the requests as cURL commands, executes the flow, and reports whether the execution succeeded.

If the flow fails, FlowProbe asks whether an impediment should be created:

```text
Do you want to create an impediment? (Y/N):
```

When confirmed, the current Azure DevOps adapter creates the configured work item and prints its ID.

---

## Flow definition

A flow contains a name and an ordered list of steps:

```yaml
name: "pokeapi-success-flow"

steps:
  - name: "get-all-pokemon"

    request:
      url: "https://pokeapi.co/api/v2/pokemon?offset=0&limit=1350"
      method: "GET"

      headers:
        accept: "application/json"

      body: null

    requires: null

    export:
      pokemonName: "results.0.name"

  - name: "get-pokemon"

    request:
      url: "https://pokeapi.co/api/v2/pokemon/${pokemonName}"
      method: "GET"

      headers:
        accept: "application/json"

      body:
        test: 1
        test2: 2

    requires:
      name: ${pokemonName}

    export: null

  - name: "get-pikachu"

    request:
      url: "https://pokeapi.co/api/v2/pokemon/pikachu"
      method: "GET"

      headers:
        accept: "application/json"

    requires: null
    export: null
```

---

## YAML reference

### Flow

| Field   | Required | Description                 |
| ------- | -------: | --------------------------- |
| `name`  |      Yes | Human-readable flow name.   |
| `steps` |      Yes | Ordered list of HTTP steps. |

### Step

| Field      | Required | Description                                                            |
| ---------- | -------: | ---------------------------------------------------------------------- |
| `name`     |      Yes | Unique descriptive name for the step.                                  |
| `request`  |      Yes | HTTP request definition.                                               |
| `requires` |       No | Conditions or data required by the step. Support is still evolving.    |
| `export`   |       No | Values extracted from the response and added to the execution context. |

### Request

| Field     | Required | Description                             |
| --------- | -------: | --------------------------------------- |
| `url`     |      Yes | Target URL. Placeholders are supported. |
| `method`  |      Yes | HTTP method such as `GET` or `POST`.    |
| `headers` |       No | Request headers.                        |
| `body`    |       No | Request body.                           |

### Export

Exports map a variable name to a path in the JSON response:

```yaml
export:
  pokemonName: "results.0.name"
```

Given a response like:

```json
{
  "results": [
    {
      "name": "bulbasaur"
    }
  ]
}
```

FlowProbe adds this value to the execution context:

```text
pokemonName = bulbasaur
```

A later step can use it:

```yaml
url: "https://pokeapi.co/api/v2/pokemon/${pokemonName}"
```

The resolved URL becomes:

```text
https://pokeapi.co/api/v2/pokemon/bulbasaur
```

---

## Failure example

A flow can deliberately target an invalid resource:

```yaml
- name: "get-invalid-pokemon"

  request:
    url: "https://pokeapi.co/api/v2/pokemon/failure"
    method: "GET"

    headers:
      accept: "application/json"

  requires: null
  export: null
```

FlowProbe keeps the generated request representation available for diagnosis and can use the failed execution to create an Azure DevOps impediment.

---

## Native executable

FlowProbe supports GraalVM Native Image.

Compile it with:

```bash
./gradlew clean nativeCompile
```

The generated executable is written to:

```text
build/native/nativeCompile/flowprobe
```

Run it:

```bash
./build/native/nativeCompile/flowprobe --help
```

Example:

```bash
./build/native/nativeCompile/flowprobe run \
  --file src/main/resources/flow-success.yaml
```

A native executable provides:

* Fast startup.
* No JVM requirement on the target machine.
* A single platform-specific executable.
* Lower runtime memory overhead for CLI usage.

Native executables are platform-specific. A binary compiled on macOS does not magically become a Windows executable because positive thinking is not a linker.

---

## Native Image metadata

FlowProbe includes Native Image metadata under:

```text
src/main/resources/META-INF/native-image/
```

This metadata is required by libraries that use reflection, JNI, dynamic proxies, serialization, or native operating-system integrations.

The current metadata supports, among other things:

* Picocli command discovery.
* Jackson serialization.
* `java-keyring`.
* macOS Keychain access.

When adding a new runtime path that relies on dynamic behavior, execute it with the Native Image tracing agent.

Example:

```bash
./gradlew runWithNativeAgent \
  -PappArgs="run --file /absolute/path/to/flow.yaml"
```

Then rebuild:

```bash
./gradlew clean nativeCompile
```

The tracing agent must execute the relevant behavior. Running only `--help` will not teach GraalVM how to serialize an Azure DevOps request or access a keyring backend. Compilers remain stubbornly unable to infer code paths you never execute.

---

## Architecture

FlowProbe follows a hexagonal architecture and separates the project into three primary areas:

```text
io.github.ctorressoftware
├── application
│   ├── port
│   │   ├── in
│   │   └── out
│   └── usecase
├── domain
│   ├── constant
│   ├── exception
│   └── model
├── infrastructure
│   ├── callservice
│   ├── cli
│   ├── persistence
│   ├── provider
│   ├── readfile
│   ├── renderer
│   └── ticket
├── AppConfig.java
└── Main.java
```

### Domain

Contains the core flow model and execution state, including concepts such as:

* `Flow`
* `FlowStep`
* `ServiceCall`
* `Context`
* `FilePath`
* request formats
* execution results and summaries

The domain does not depend on Picocli, SnakeYAML, Azure DevOps, Jackson, or the operating system's credential store.

### Application

Contains:

* Input ports representing use cases.
* Output ports representing external capabilities.
* Use-case implementations.
* Flow orchestration.

Relevant abstractions include:

* `ReadFileUseCase`
* `ExecuteFlowUseCase`
* `CreateImpedimentTicketUseCase`
* `ConfigureProviderUseCase`
* `FlowFileReader`
* `ServiceCaller`
* `RequestRenderer`
* `ProviderPrompt`
* `ProviderConfigurator`
* `ProviderConfigRepository`
* `CredentialsStorageManager`
* `ImpedimentTicketCreator`

### Infrastructure

Contains concrete adapters:

* Picocli commands.
* YAML file reader.
* Java HTTP client integration.
* cURL renderer.
* Azure provider prompt and configuration.
* Native credential storage through `java-keyring`.
* Azure DevOps work-item client.

### Dependency composition

FlowProbe does not use Spring or another dependency-injection container.

`AppConfig` acts as the composition root and manually creates and connects the application graph:

```text
Main
  └── AppConfig
       ├── input adapters
       ├── use cases
       ├── output ports
       └── infrastructure adapters
```

The application receives dependencies through constructors. Infrastructure implementations are selected only during bootstrap.

No global `getInstance()` ceremony. No static dependency swamp. Just explicit object construction, as Java intended before frameworks convinced everyone that constructors were advanced technology.

---

## Credential storage

FlowProbe separates provider interaction from credential persistence through application ports.

The current implementation uses `java-keyring`, allowing credentials to be stored through the native facilities available on the operating system.

Examples include:

* macOS Keychain
* Windows Credential Manager
* Linux-compatible keyring backends

Only macOS Keychain has been verified in the current development environment. Other operating systems require dedicated runtime and native-image testing.

Secrets must never be:

* Printed to the console.
* Included in generated cURL output.
* Written to logs.
* Committed to Git.
* Stored in sample YAML files.

---

## Current limitations

FlowProbe is still an early-stage project.

Current limitations include:

* Azure DevOps is the only supported ticket provider.
* Only `configure` and `run` are implemented as subcommands.
* cURL is the only active request renderer.
* Native Image behavior has been verified primarily on macOS.
* Metadata may need to be regenerated for additional operating systems and providers.
* The YAML contract may evolve.
* `requires` behavior is still under development.
* Error output and exit-code handling need further refinement.
* Automated test coverage is not yet complete.
* No public binary release or package-manager installation is available yet.

---

## Roadmap

Planned improvements include:

* `flowprobe status <provider|all>`
* `flowprobe remove <provider|all>`
* Additional ticket providers such as Jira
* More request renderers:

    * Wget
    * JavaScript Fetch
    * Python Requests
    * PowerShell
    * Hurl
    * Raw HTTP
* Stronger response assertions
* Better `requires` validation
* Configurable expected HTTP status codes
* Timeouts and retry policies
* Non-interactive execution for CI/CD
* Structured execution reports
* Improved error messages and exit codes
* Unit and integration tests
* Native builds for macOS, Linux, and Windows
* GitHub releases
* Homebrew distribution
* Shell completion
* Multiple-flow and directory execution

---

## Development commands

Run tests:

```bash
./gradlew test
```

Build:

```bash
./gradlew clean build
```

Run the CLI:

```bash
./gradlew run --args="--help"
```

Configure Azure:

```bash
./gradlew run --args="configure azure"
```

Execute a sample flow:

```bash
./gradlew run \
  --args="run --file src/main/resources/flow-success.yaml"
```

Compile native image:

```bash
./gradlew clean nativeCompile
```

Run with the Native Image agent:

```bash
./gradlew runWithNativeAgent \
  -PappArgs="configure azure"
```

---

## Contributing

FlowProbe is still defining its public contract.

Before implementing a large feature:

1. Open an issue describing the problem.
2. Keep provider-specific behavior behind application ports.
3. Avoid introducing infrastructure dependencies into the domain or application layers.
4. Add tests for new parsing, execution, and provider behavior.
5. Verify regular JVM execution.
6. Verify Native Image execution when the change uses reflection, serialization, JNI, proxies, or native libraries.

Small, focused pull requests are easier to review than heroic refactors that replace half the project and leave everyone pretending the architecture is clearer.

---

## Author

Created by [Carlos Torres](https://github.com/ctorressoftware).

---

## Disclaimer

FlowProbe executes HTTP requests and can create work items in external platforms.

Review flow definitions before running them against production environments. Use provider credentials with the minimum required permissions and never commit real secrets to the repository.
