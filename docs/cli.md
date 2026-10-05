# CLI reference

FlowProbe uses Picocli and exposes a small command surface centered on `run` and `configure`.

## Root command

```bash
flowprobe
```

Running FlowProbe without a subcommand prints the root usage information.

Standard help and version options are available:

```bash
flowprobe --help
flowprobe -h

flowprobe --version
flowprobe -V
```

The version reported by the CLI comes from the Gradle project version through the generated `version.properties` resource.

## Run a flow

Long form:

```bash
flowprobe run --file /path/to/flow.yaml
```

Short form:

```bash
flowprobe run -f /path/to/flow.yaml
```

`--file` / `-f` is required and must point to a local YAML file.

Command help:

```bash
flowprobe run --help
```

### Create an impediment on failure

```bash
flowprobe run \
  -f /path/to/flow.yaml \
  --create-impediment
```

Ticket creation is opt-in and only happens when the flow fails.

## Configure a provider

```bash
flowprobe configure azure
```

Command help:

```bash
flowprobe configure --help
```

The provider argument is converted before configuration runs. Unsupported values are rejected as invalid CLI input instead of leaking an internal Java enum exception.

Current provider:

```text
azure
```

Example invalid input:

```bash
flowprobe configure jira
```

FlowProbe returns exit code `2` for invalid command arguments.

## Exit codes

| Code | Meaning                                 |
|-----:|-----------------------------------------|
|  `0` | Command or flow completed successfully. |
|  `1` | Flow execution or runtime error.        |
|  `2` | Invalid CLI arguments.                  |

## Output behavior

A successful step is rendered with `✓`; a failed step is rendered with `✗`.

Example:

```text
FlowProbe · controlled-failure

✓ 1. get-bulbasaur
  GET https://pokeapi.co/api/v2/pokemon/bulbasaur
  Validation  passed

✗ 2. fail-on-purpose
  GET https://pokeapi.co/api/v2/pokemon/pikachu
  Validation  failed

  Reproduce
    curl -X GET -H 'accept: application/json' 'https://pokeapi.co/api/v2/pokemon/pikachu'

Flow failed · 1/2 steps passed
```

When a failed request is rendered as cURL, FlowProbe redacts these common sensitive headers:

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

Header redaction does not inspect arbitrary secrets embedded in URLs or request bodies.
