# Flow format

FlowProbe reads ordered HTTP flows from YAML.

A flow has a name and a non-empty list of steps.

## Minimal flow

```yaml
name: "basic-example"

steps:
  - name: "get-pikachu"
    request:
      url: "https://pokeapi.co/api/v2/pokemon/pikachu"
      method: "GET"

    expect:
      status: 200
```

## Flow

| Field   | Required | Description                            |
|---------|---------:|----------------------------------------|
| `name`  |      Yes | Human-readable flow name.              |
| `steps` |      Yes | Ordered, non-empty list of flow steps. |

A missing or blank flow name is rejected. A missing or empty `steps` list is also rejected.

## Step

| Field     | Required | Description                                 |
|-----------|---------:|---------------------------------------------|
| `name`    |      Yes | Step name.                                  |
| `request` |      Yes | HTTP request definition.                    |
| `expect`  |       No | Response expectations.                      |
| `exports` |       No | JSON values exported into the flow context. |

A step must have a non-blank name and a request.

## Request

| Field     | Required | Description                                                                          |
|-----------|---------:|--------------------------------------------------------------------------------------|
| `url`     |      Yes | Target URL. Placeholders are supported.                                              |
| `method`  |      Yes | HTTP method string. Placeholders are supported.                                      |
| `headers` |       No | HTTP headers. Placeholder interpolation is supported in names and values.            |
| `body`    |       No | Structured request body. Maps, lists, scalar values, and placeholders are supported. |

Example:

```yaml
request:
  url: "https://example.test/users/${userId}"
  method: "POST"
  headers:
    accept: "application/json"
    content-type: "application/json"
  body:
    id: "${userId}"
    enabled: true
```

When a body is present, FlowProbe serializes it as JSON before sending the request.

## Status expectations

Exact status:

```yaml
expect:
  status: 200
```

If `expect` is omitted, FlowProbe accepts any HTTP status in the `200-299` range.

If `expect` exists but `status` is omitted, the same default `200-299` success rule is applied.

## Body expectations

Body expectations use JSON Pointer paths:

```yaml
expect:
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

Multiple expectations can be declared:

```yaml
expect:
  status: 200
  body:
    - path: "/name"
      operator: "equals"
      value: "bulbasaur"

    - path: "/name"
      operator: "notEquals"
      value: "pikachu"
```

FlowProbe extracts the actual JSON value from each path and evaluates it against the expected value.

## Exports

Exports map a context variable name to a JSON Pointer path:

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

the exported values keep their JSON types:

```text
userId  -> number 25
enabled -> boolean true
```

Exports are only written after the step response passes validation.

A missing export path causes execution to fail rather than silently producing an empty value.

## Fail-fast execution

Steps are executed in declaration order.

For each step, FlowProbe performs this sequence:

```text
resolve placeholders
       ↓
execute HTTP request
       ↓
validate response
       ↓
export response values
       ↓
continue
```

If a step fails validation:

- the failed step is recorded;
- its exports are not added to the context;
- subsequent steps are not executed;
- the flow returns a non-zero exit code.

See [`examples/controlled-failure.yaml`](../examples/controlled-failure.yaml) for a deliberate fail-fast example.

## Complete examples

Useful examples in the repository:

- [`basic.yaml`](../examples/basic.yaml) — one request with status validation.
- [`expectations.yaml`](../examples/expectations.yaml) — status and JSON body expectations.
- [`exports.yaml`](../examples/exports.yaml) — multi-step export and reuse.
- [`normal-flow.yaml`](../examples/normal-flow.yaml) — typed exports, nested placeholders, multiple expectations, and a longer chain.
- [`request-body-flow.yaml`](../examples/request-body-flow.yaml) — nested JSON body serialization with typed placeholders.
- [`controlled-failure.yaml`](../examples/controlled-failure.yaml) — intentional validation failure and fail-fast behavior.
