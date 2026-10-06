# FlowProbe

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![GraalVM Native Image](https://img.shields.io/badge/GraalVM-Native%20Image-blue.svg)](https://www.graalvm.org/latest/reference-manual/native-image/)
[![License](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](LICENSE)

**FlowProbe** is a Java command-line tool for defining, executing, and validating multistep HTTP flows from YAML.

A flow can call an endpoint, validate its response, export values from the JSON response, and reuse them in later requests and expectations.

It stops on the first failed step, renders a reproducible cURL command, and can optionally create an Azure DevOps work item with the failure context.

> **Project status:** `v0.1.0-rc.4` is the current release candidate and is intended as the final validation candidate before `v0.1.0`.

---

## Why FlowProbe?

FlowProbe was born from a practical Dev/QA need: verifying an environment often requires more than checking whether a single endpoint is alive.

Real verification flows may require calling one service, validating its response, extracting data from it, reusing that data in another request, and continuing through several dependent steps. When something breaks, the team also needs enough context to reproduce and report the failure quickly.

These procedures are often repeated manually or shared as instructions, making them harder to reproduce consistently across a team.

FlowProbe turns that process into a portable YAML definition that can be versioned, shared, and executed the same way by developers, QA engineers, or automated environments.

A FlowProbe flow can:

1. Execute an ordered sequence of HTTP requests.
2. Validate each response.
3. Export values and reuse them in later steps and expectations.
4. Stop immediately at the first failure.
5. Produce a reproducible request for investigation.
6. Optionally create an Azure DevOps work item with the failure context.

The goal is not to replace test frameworks or API clients, but to provide a lightweight, declarative, and repeatable way to describe operational verification flows across Dev and QA environments.

Because the flow lives in YAML, the same verification can be reviewed in source control, shared across a team, executed locally, or automated from CI/CD.

---

## Features

- Ordered multi-step HTTP flows defined in YAML.
- Exact status expectations and default `2xx` success behavior.
- JSON body expectations using JSON Pointer paths.
- `equals` and `notEquals` expectation operators.
- Typed JSON exports reused through `${placeholders}`.
- Placeholder resolution in URLs, HTTP methods, headers, object keys, request bodies, lists, and response expectation values.
- Type preservation for exact placeholders.
- Fail-fast execution.
- Reproducible cURL output for failed requests.
- Redaction of common sensitive HTTP headers in rendered cURL output.
- Optional Azure DevOps work-item creation after a failed flow.
- Azure DevOps configuration stored through the operating-system credential store.
- GraalVM Native Image support.
- Native macOS builds for Intel and Apple Silicon.
- Contextual CLI help and concise invalid-argument handling.

---

## Installation

### Homebrew

```bash
brew install ctorressoftware/tap/flowprobe
```

Upgrade an existing installation:

```bash
brew update
brew upgrade ctorressoftware/tap/flowprobe
```

Verify the installation:

```bash
flowprobe --version
```

Expected for this release candidate:

```text
flowprobe 0.1.0-rc.4
```

### Build from source

Requirements:

- Java 21
- Gradle Wrapper
- GraalVM for JDK 21 when compiling the native executable

Clone and verify:

```bash
git clone https://github.com/ctorressoftware/flow-probe.git
cd flow-probe
./gradlew clean check
```

Run through the JVM:

```bash
./gradlew run --args="--help"
```

Build the native executable:

```bash
./gradlew nativeCompile
```

The native executable is generated at:

```text
build/native/nativeCompile/flowprobe
```

---

## Quick start

Create a flow:

```yaml
name: "pokemon-check"

steps:
  - name: "get-pikachu"
    request:
      url: "https://pokeapi.co/api/v2/pokemon/pikachu"
      method: "GET"
      headers:
        accept: "application/json"

    expect:
      status: 200
      body:
        - path: "/name"
          operator: "equals"
          value: "pikachu"
```

Run it:

```bash
flowprobe run -f flow.yaml
```

A successful execution prints a compact per-step summary:

```text
FlowProbe · pokemon-check

✓ get-pikachu
  GET https://pokeapi.co/api/v2/pokemon/pikachu
  Validation  passed

Flow passed · 1/1 steps
```

For a multistep example with exports and placeholders, see [`examples/normal-flow.yaml`](examples/normal-flow.yaml).

---

## CLI

```text
flowprobe
flowprobe --help
flowprobe --version

flowprobe run --file <path>
flowprobe run -f <path>
flowprobe run -f <path> --create-impediment

flowprobe configure <provider>
```

Currently supported provider:

```text
azure
```

Command-specific help:

```bash
flowprobe run --help
flowprobe configure --help
```

Invalid CLI arguments return exit code `2`. Flow execution/runtime failures return exit code `1`.

See the [CLI reference](docs/cli.md) for command behavior and examples.

---

## Flow example with exports

```yaml
name: "exports-example"

steps:
  - name: "get-pokemon-list"
    request:
      url: "https://pokeapi.co/api/v2/pokemon?limit=1"
      method: "GET"

    exports:
      pokemonName: "/results/0/name"

  - name: "get-exported-pokemon"
    request:
      url: "https://pokeapi.co/api/v2/pokemon/${pokemonName}"
      method: "GET"

    expect:
      body:
        - path: "/name"
          operator: "equals"
          value: "${pokemonName}"
```

Exact placeholders preserve the exported JSON type when used in structured values and expectations.

See:

- [Flow format](docs/flow-format.md)
- [Placeholders and type preservation](docs/placeholders.md)

---

## Azure DevOps integration

Configure Azure DevOps:

```bash
flowprobe configure azure
```

Create a work item when a flow fails:

```bash
flowprobe run -f flow.yaml --create-impediment
```

Configuration is stored through `java-keyring` in the operating-system credential store rather than a project-local plain-text configuration file.

See [Azure DevOps integration](docs/azure-devops.md).

---

## Examples

The repository includes:

```text
examples/
├── basic.yaml
├── controlled-failure.yaml
├── expectations.yaml
├── exports.yaml
├── normal-flow.yaml
└── request-body-flow.yaml
```

The examples cover basic status validation, body expectations, exports, typed placeholders, nested request bodies, controlled failures, and fail-fast execution.

---

## Documentation

- [CLI reference](docs/cli.md)
- [Flow format](docs/flow-format.md)
- [Placeholders and type preservation](docs/placeholders.md)
- [Azure DevOps integration](docs/azure-devops.md)
- [Native Image](docs/native-image.md)
- [Architecture](docs/architecture.md)
- [Development guide](docs/development.md)

---

## Current limitations

- Official pre-release binaries are currently published only for macOS x64 (Intel) and macOS arm64 (Apple Silicon).
- Azure DevOps is the only implemented ticket provider.
- cURL is the only request renderer currently exposed.
- Body expectations currently support only `equals` and `notEquals`.
- Explicit `value: null` body expectations are not yet supported.
- Placeholder interpolation in URLs is textual; FlowProbe does not automatically URL-encode user-provided placeholder values.
- Failed body expectations are tracked internally, but the CLI does not yet display which expectation failed or its expected and actual values.
- Step execution duration is not yet measured.
- Retry policies are not implemented.

---

## Contributing

Contributions are welcome. See [CONTRIBUTING.md](CONTRIBUTING.md).

Before submitting a pull request:

```bash
./gradlew clean check
```

---

## Current version

**Release candidate:** `v0.1.0-rc.4`

See [GitHub Releases](https://github.com/ctorressoftware/flow-probe/releases) for published releases and [CHANGELOG.md](CHANGELOG.md) for version history.

---

## Security

Do not report vulnerabilities or expose credentials in public issues.

See [SECURITY.md](SECURITY.md).

---

## License

FlowProbe is licensed under the [Apache License 2.0](LICENSE).

---

## Author

Created by [Carlos Torres](https://github.com/ctorressoftware).
