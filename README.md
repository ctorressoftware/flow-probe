# FlowProbe

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![GraalVM Native Image](https://img.shields.io/badge/GraalVM-Native%20Image-blue.svg)](https://www.graalvm.org/latest/reference-manual/native-image/)
[![License](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](LICENSE)

**FlowProbe** is a Java command-line tool for defining, executing, and validating multi-step HTTP flows from YAML.

A flow can call an endpoint, validate its response, export values from the returned JSON, reuse those values in later requests, and stop immediately when a step fails. When a failure occurs, FlowProbe can render reproducible cURL commands and optionally create an Azure DevOps work item with the failure context.

> **Project status:** `v0.1.0-rc.1` is publicly available as the first release candidate. Core behavior is feature-frozen for the first stable release; current work is focused on fixing release-blocking issues, strengthening native-runtime validation, and preparing the next release candidate.

---

## Why FlowProbe?

A useful environment check is often more than a single health endpoint.

A real workflow may need to:

1. Call one service.
2. Validate the response.
3. Extract an identifier or other value.
4. Inject that value into another request.
5. Validate the next response.
6. Stop immediately if any step fails.
7. Produce enough information to reproduce the failure.

FlowProbe keeps that workflow in a portable YAML file that can be executed locally or, as the project evolves, from CI/CD environments.

---

## Features

- Define ordered HTTP flows in YAML.
- Execute multi-step HTTP requests.
- Validate HTTP status codes.
- Validate JSON response values with `equals` and `notEquals` expectations.
- Default to accepting any `2xx` response when no explicit status is configured.
- Export values from JSON responses using JSON Pointer paths.
- Preserve exported JSON scalar types such as strings, numbers, booleans, and `null`.
- Resolve placeholders in URLs, headers, request bodies, and nested body structures.
- Preserve the original type when a body value is exactly a placeholder.
- Stop execution after the first failed step.
- Return non-zero exit codes for failed flows and invalid CLI arguments.
- Render executed requests as reproducible cURL commands.
- Redact common sensitive HTTP headers from rendered cURL output.
- Optionally create an Azure DevOps work item when a flow fails.
- Store Azure DevOps configuration through the operating system credential store using `java-keyring`.
- Build as a GraalVM Native Image executable.

---

## Technology stack

- Java 21
- Gradle Kotlin DSL
- Picocli
- SnakeYAML
- Java HTTP Client
- Jackson
- java-keyring
- GraalVM Native Image
- JUnit 5
- Mockito
- JaCoCo

---

## Installation

### Homebrew

FlowProbe is currently available as a release candidate for macOS on both Intel and Apple Silicon.

```bash
brew install ctorressoftware/tap/flowprobe
```

Verify the installation:

```bash
flowprobe --version
```

Current release candidate:

```text
flowprobe 0.1.0-rc.1
```

### Known issue in v0.1.0-rc.1

The native `configure azure` command currently fails while serializing the Azure DevOps configuration because required Native Image reflection metadata is missing.

This issue affects the `v0.1.0-rc.1` native binaries and will be addressed in the next release candidate.

### Requirements

For JVM execution:

- Java 21

For native compilation:

- GraalVM for JDK 21
- Native Image support available in the selected GraalVM distribution

---

## Build from source

Clone the repository:

```bash
git clone https://github.com/ctorressoftware/flow-probe.git
cd flow-probe
```

Run the full verification suite:

```bash
./gradlew clean check
```

Show CLI help:

```bash
./gradlew run --args="--help"
```

Show the current version:

```bash
./gradlew run --args="--version"
```

---

## Quick start

If you cloned the repository, you can run the included examples with the installed FlowProbe binary:

```bash
flowprobe run --file examples/basic.yaml
```

Run an example with response expectations:

```bash
flowprobe run --file examples/expectations.yaml
```

Run a multi-step flow that exports a value and reuses it in the next request:

```bash
flowprobe run --file examples/exports.yaml
```

If you are developing FlowProbe from source, the same examples can be executed through Gradle:

```bash
./gradlew run --args="run --file examples/basic.yaml"
```

The examples currently use the public PokéAPI and therefore require network access.

---

## CLI

Currently implemented commands:

```text
flowprobe run --file <path>
flowprobe configure <provider>
flowprobe --help
flowprobe --version
```

Currently supported provider:

```text
azure
```

### Run a flow

Using Gradle:

```bash
./gradlew run --args="run --file /absolute/path/to/flow.yaml"
```

Using a native executable:

```bash
./build/native/nativeCompile/flowprobe run \
  --file /absolute/path/to/flow.yaml
```

### Create an Azure DevOps work item on failure

Ticket creation is opt-in and non-interactive during `run`:

```bash
./gradlew run \
  --args="run --file /absolute/path/to/flow.yaml --create-impediment"
```

The work item is created only if the flow fails.

---

## Flow definition

A flow contains a name and an ordered list of steps.

```yaml
name: "pokemon-flow"

steps:
  - name: "get-pokemon-list"
    request:
      url: "https://pokeapi.co/api/v2/pokemon?limit=1"
      method: "GET"
      headers:
        accept: "application/json"

    expect:
      status: 200

    exports:
      pokemonName: "/results/0/name"

  - name: "get-exported-pokemon"
    request:
      url: "https://pokeapi.co/api/v2/pokemon/${pokemonName}"
      method: "GET"
      headers:
        accept: "application/json"

    expect:
      status: 200
      body:
        - path: "/name"
          operator: "equals"
          value: "${pokemonName}"
```

---

## YAML reference

### Flow

| Field | Required | Description |
| --- | ---: | --- |
| `name` | Yes | Human-readable flow name. |
| `steps` | Yes | Ordered list of HTTP steps. |

### Step

| Field | Required | Description |
| --- | ---: | --- |
| `name` | Yes | Step name. |
| `request` | Yes | HTTP request definition. |
| `expect` | No | Response expectations. |
| `exports` | No | Values extracted from the response and added to the execution context. |

### Request

| Field | Required | Description |
| --- | ---: | --- |
| `url` | Yes | Target URL. Placeholders are supported. |
| `method` | Yes | HTTP method. |
| `headers` | No | HTTP headers. Placeholders are supported in names and values. |
| `body` | No | Request body. Maps, lists, scalar values, and placeholders are supported. |

### Expectations

An explicit status expectation:

```yaml
expect:
  status: 200
```

If `expect` is omitted, or `expect.status` is omitted, FlowProbe considers any status in the `200-299` range successful.

Body expectations use JSON Pointer paths:

```yaml
expect:
  status: 200
  body:
    - path: "/name"
      operator: "equals"
      value: "pikachu"
```

Currently supported operators:

```text
equals
notEquals
```

Example:

```yaml
expect:
  body:
    - path: "/active"
      operator: "equals"
      value: true

    - path: "/status"
      operator: "notEquals"
      value: "disabled"
```

Paths are JSON Pointer expressions and must begin with `/`.

### Exports

Exports map a context variable name to a JSON Pointer path in the response:

```yaml
exports:
  userId: "/user/id"
  enabled: "/user/enabled"
```

Given:

```json
{
  "user": {
    "id": 25,
    "enabled": true
  }
}
```

FlowProbe stores the values with their JSON types preserved:

```text
userId  -> number 25
enabled -> boolean true
```

A missing export path causes execution to fail instead of silently producing an empty value.

---

## Placeholders and type preservation

Placeholders use this syntax:

```text
${variableName}
```

For URLs and headers, interpolation is textual:

```yaml
url: "https://example.test/users/${userId}"
headers:
  X-Enabled: "${enabled}"
```

If `userId` is the number `25` and `enabled` is the boolean `true`, the resulting HTTP values are text:

```text
https://example.test/users/25
X-Enabled: true
```

Request bodies preserve types when a value is exactly one placeholder:

```yaml
body:
  id: "${userId}"
  enabled: "${enabled}"
  message: "user-${userId}"
```

With `userId = 25` and `enabled = true`, the serialized JSON is:

```json
{
  "id": 25,
  "enabled": true,
  "message": "user-25"
}
```

This distinction allows exported JSON values to remain correctly typed across multiple HTTP steps.

---

## Execution behavior

FlowProbe executes steps in declaration order.

For each step it performs the following sequence:

```text
resolve placeholders
       ↓
execute HTTP request
       ↓
validate response
       ↓
export response values
       ↓
continue to next step
```

If validation fails:

- the failed step is recorded as unsuccessful;
- its exports are not added to the context;
- subsequent steps are not executed;
- the flow exits with a non-zero code.

---

## Exit codes

| Code | Meaning |
| ---: | --- |
| `0` | Flow completed successfully. |
| `1` | Flow execution or runtime error. |
| `2` | Invalid CLI arguments. |

These exit codes make FlowProbe suitable for scripting and future CI/CD integration.

---

## Reproducible cURL output

FlowProbe renders executed requests as cURL commands so a request can be reproduced outside the tool.

Common sensitive headers are redacted, including headers such as:

```text
Authorization
Proxy-Authorization
Cookie
X-API-Key
Api-Key
X-Auth-Token
X-Access-Token
X-Amz-Security-Token
```

Example:

```text
Authorization: <redacted>
```

Header redaction does not currently attempt to detect arbitrary secrets embedded in URLs or request bodies. Avoid placing credentials directly in flow definitions.

---

## Azure DevOps integration

Azure DevOps is currently the only ticket provider wired into FlowProbe.

### Configure Azure DevOps

```bash
./gradlew run --args="configure azure"
```

FlowProbe asks for:

```text
Azure DevOps organization
Azure DevOps project
Azure DevOps work item type
Azure DevOps Personal Access Token (PAT)
```

The configuration is serialized and stored through `java-keyring` in the operating system credential store instead of a plain-text project configuration file.

When possible, run configuration from a real terminal so the PAT can be read through `Console.readPassword` without echoing it. Environments without an attached Java `Console` currently fall back to regular standard-input reading.

### PAT scope

Use the narrowest Azure DevOps PAT permission required for work-item creation. Azure DevOps documents `vso.work_write` as the scope that grants read/create/update access to work items.

Official API documentation:

https://learn.microsoft.com/en-us/rest/api/azure/devops/wit/work-items/create?view=azure-devops-rest-7.1

### Create a work item after a failed flow

```bash
./gradlew run \
  --args="run --file /path/to/flow.yaml --create-impediment"
```

FlowProbe uses the failed request information to build the work-item description. Rendered sensitive headers are redacted before being included.

---

## Examples

The repository includes:

```text
examples/
├── basic.yaml
├── expectations.yaml
└── exports.yaml
```

- `basic.yaml` — one request with status validation.
- `expectations.yaml` — status and JSON body expectations.
- `exports.yaml` — multi-step execution with an exported placeholder.

---

## Native executable

FlowProbe supports GraalVM Native Image.

Compile:

```bash
./gradlew clean nativeCompile
```

The executable is generated at:

```text
build/native/nativeCompile/flowprobe
```

Run it:

```bash
./build/native/nativeCompile/flowprobe --help
```

Native executables are platform-specific.

The current release verification workflow builds and verifies FlowProbe natively on both macOS x64 (Intel) and macOS arm64 (Apple Silicon). For each architecture, the native executable:

- starts successfully;
- reports its version and help output;
- reads and executes a YAML flow;
- performs a real HTTP request against a temporary local server;
- validates the response and exits successfully.

The workflow also runs a separate integration test against a temporary macOS Keychain to exercise the operating-system credential-store integration.

### Native Image metadata

Reachability metadata required by the native executable is committed under:

```text
src/main/resources/META-INF/native-image/
```

Native Image agent filters used for metadata maintenance are kept under:

```text
native-image/filters/
```

Changes involving reflection, serialization, JNI, proxies, SnakeYAML mapping, or native integrations should be validated with `nativeCompile` and a representative native smoke test. Tracing-agent output should be reviewed before being merged into the committed metadata.

---

## Architecture

FlowProbe uses a hexagonal architecture with explicit dependency wiring.

```text
io.github.ctorressoftware
├── domain
│   ├── constant
│   ├── exception
│   └── model
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

`AppConfig` is the composition root. FlowProbe does not use a dependency-injection framework; dependencies are connected explicitly through constructors.

The main boundaries are:

- **Domain** — flow, step, expectations, context, requests, execution summaries.
- **Application** — use cases, orchestration, validation logic, ports.
- **Infrastructure** — Picocli, SnakeYAML, Jackson, HTTP, credential storage, Azure DevOps, and cURL rendering.

---

## Testing

Run the standard test suite:

```bash
./gradlew test
```

Run the full JVM verification suite, including JaCoCo coverage verification:

```bash
./gradlew clean check
```

The project includes local HTTP end-to-end tests using the JDK `HttpServer`. These tests verify multi-step execution, typed exports, real request-body serialization, expectations, and fail-fast behavior without depending on an external service.

The operating-system credential-store integration test is isolated from the standard test task:

```bash
./gradlew osKeystoreTest
```

It requires an environment with a supported operating-system credential store. The macOS Native Verification GitHub Actions workflow creates a temporary Keychain, runs this integration test, builds the Native Image executable, and executes native smoke tests.

---

## Current limitations

- Official pre-release binaries are currently available only for macOS x64 (Intel) and macOS arm64 (Apple Silicon).
- Azure DevOps is the only implemented ticket provider.
- cURL is the only request renderer currently exposed.
- Body expectations currently support only `equals` and `notEquals`.
- Explicit `value: null` body expectations are not yet supported.
- Release-oriented native verification currently covers macOS x64 (Intel) and macOS arm64 (Apple Silicon); other operating systems are not yet published.
- Placeholder interpolation in URLs is textual; FlowProbe does not automatically URL-encode user-provided placeholder values.
- Execution summaries do not yet expose full expectation-level failure details.
- Step execution duration is not yet measured.
- Retry policies are not implemented.

---

## Roadmap

The immediate release path is:

- fix release-blocking issues discovered in `v0.1.0-rc.1`;
- strengthen Native Image metadata coverage using representative real application flows;
- publish `v0.1.0-rc.2`;
- continue validating installation and real-world usage;
- publish the first stable `v0.1.0` release.

Possible later improvements include:

- Additional expectation operators.
- Environment and initial-context variables.
- More ticket providers.
- Additional request renderers.
- Structured execution reports and richer failure diagnostics.
- Multi-flow, directory, and suite execution.
- Additional operating-system and architecture builds.
- Native Image metadata drift detection for dependency and native-sensitive changes.

---

## Changelog

See [CHANGELOG.md](CHANGELOG.md) for notable changes by release.

---

## Contributing

Contributions are welcome. See [CONTRIBUTING.md](CONTRIBUTING.md).

Before submitting a pull request:

```bash
./gradlew clean check
```

---

## Security

Please do not report vulnerabilities or expose credentials in public issues.

See [SECURITY.md](SECURITY.md) for the current reporting policy.

---

## License

FlowProbe is licensed under the [Apache License 2.0](LICENSE).

---

## Author

Created by [Carlos Torres](https://github.com/ctorressoftware).
