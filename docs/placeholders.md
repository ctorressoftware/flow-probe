# Placeholders and type preservation

FlowProbe uses `${variableName}` placeholders to reuse exported values in later steps.

Variables come from `exports` in previously successful steps.

## Basic export and reuse

```yaml
steps:
  - name: "get-pokemon-list"
    request:
      url: "https://pokeapi.co/api/v2/pokemon?limit=1"
      method: "GET"

    exports:
      pokemonName: "/results/0/name"

  - name: "get-pokemon"
    request:
      url: "https://pokeapi.co/api/v2/pokemon/${pokemonName}"
      method: "GET"
```

If the first response exports `"bulbasaur"`, the next URL becomes:

```text
https://pokeapi.co/api/v2/pokemon/bulbasaur
```

## Text interpolation

When a placeholder appears inside a larger string, FlowProbe converts the value to text:

```yaml
headers:
  x-flowprobe-description: "pokemon-${pokemonName}"
```

With `pokemonName = "bulbasaur"`:

```text
pokemon-bulbasaur
```

This textual interpolation behavior applies to URLs, HTTP methods, header names, header values, and other strings.

## Exact placeholders preserve type

When the entire structured value is exactly one placeholder, FlowProbe keeps the original exported value instead of converting it to text.

Example:

```yaml
body:
  id: "${pokemonId}"
  hidden: "${hiddenAbility}"
```

If the context contains:

```text
pokemonId      -> number 1
hiddenAbility  -> boolean false
```

the serialized JSON remains typed:

```json
{
  "id": 1,
  "hidden": false
}
```

It does not become:

```json
{
  "id": "1",
  "hidden": "false"
}
```

## Nested maps and lists

Resolution is recursive:

```yaml
body:
  pokemon:
    id: "${pokemonId}"
    name: "${pokemonName}"

  description: "pokemon-${pokemonName}"

  tags:
    - "${pokemonName}"
    - "flowprobe"

  metadata:
    enabled: true
    attempts: 3
```

Exact placeholders inside nested maps and lists preserve their original types.

## Placeholder resolution in object keys

Map keys are resolved textually as well.

Keys must be strings.

## Response expectations

Placeholders can also be used in expected response values:

```yaml
expect:
  body:
    - path: "/id"
      operator: "equals"
      value: "${pokemonId}"
```

If `pokemonId` is the number `1`, the expectation compares against numeric `1`, not string `"1"`.

Inline interpolation also works:

```yaml
expect:
  body:
    - path: "/description"
      operator: "equals"
      value: "pokemon-${pokemonName}"
```

## Missing variables

A placeholder must refer to a variable already present in the flow context.

Using an undefined variable causes execution to fail with a missing-variable error. FlowProbe does not silently leave unresolved placeholders in place.

## URL encoding

Placeholder interpolation in URLs is textual.

FlowProbe does not currently URL-encode arbitrary placeholder values automatically. If a value can contain URI-sensitive characters, prepare the flow accordingly.
