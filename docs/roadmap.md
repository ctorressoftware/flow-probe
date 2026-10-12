# Roadmap

This document tracks planned improvements and ideas for future FlowProbe releases.

Items listed here are not guaranteed for a specific release unless explicitly stated.

---

## Response expectation operators

FlowProbe currently supports:

- `equals`
- `notEquals`
- `contains`

The expectation system is intentionally extensible through dedicated evaluators. Future releases may expand the available operators.

### Presence and nullability

Planned operators:

- `exists`
- `notExists`
- `isNull`
- `notNull`

Examples:

```yaml
expect:
  body:
    - path: "/user/id"
      operator: "exists"

    - path: "/error"
      operator: "notExists"

    - path: "/deletedAt"
      operator: "isNull"

    - path: "/token"
      operator: "notNull"
```

These operators should not require a `value`.

---

### Numeric comparisons

Planned operators:

- `greaterThan`
- `greaterThanOrEquals`
- `lessThan`
- `lessThanOrEquals`

Example:

```yaml
expect:
  body:
    - path: "/total"
      operator: "greaterThan"
      value: 0
```

Numeric operators should validate compatible numeric values without forcing callers to convert numbers to strings.

---

### String matching

Planned operators:

- `matches`
- `startsWith`
- `endsWith`

Examples:

```yaml
expect:
  body:
    - path: "/reference"
      operator: "startsWith"
      value: "PAY-"

    - path: "/filename"
      operator: "endsWith"
      value: ".json"

    - path: "/code"
      operator: "matches"
      value: "^ORD-[0-9]+$"
```

`matches` is intended to support regular-expression validation.

---

### Multiple accepted values

Planned operators:

- `oneOf`
- `notOneOf`

Example:

```yaml
expect:
  body:
    - path: "/status"
      operator: "oneOf"
      value:
        - "READY"
        - "PROCESSING"
        - "COMPLETED"
```

This is intended for responses where several values are valid without requiring multiple separate expectations.

---

### Collection and content checks

Planned operators:

- `empty`
- `notEmpty`
- `sizeEquals`
- `sizeGreaterThan`
- `sizeLessThan`

Examples:

```yaml
expect:
  body:
    - path: "/results"
      operator: "notEmpty"

    - path: "/results"
      operator: "sizeGreaterThan"
      value: 0
```

The exact semantics for `null`, strings, arrays, and objects should be defined before implementation.

---

### JSON type validation

Planned operator:

- `typeIs`

Example:

```yaml
expect:
  body:
    - path: "/items"
      operator: "typeIs"
      value: "array"
```

Potential supported types:

- `string`
- `number`
- `boolean`
- `object`
- `array`
- `null`

This would allow FlowProbe to validate response shape without requiring an exact value.

---

## `contains` semantics

The current `contains` operator should eventually have clearly defined behavior for different JSON value types.

Potential semantics:

```text
String  contains String
Array   contains value
Object  contains key
```

Example:

```yaml
expect:
  body:
    - path: "/roles"
      operator: "contains"
      value: "ADMIN"
```

Given:

```json
{
  "roles": ["USER", "ADMIN"]
}
```

the expectation could evaluate successfully.

This behavior should be explicitly defined and tested before expanding `contains` beyond its current implementation.

---

## Suggested implementation order

### Phase 1

- `exists`
- `notExists`
- `isNull`
- `notNull`

### Phase 2

- `greaterThan`
- `greaterThanOrEquals`
- `lessThan`
- `lessThanOrEquals`

### Phase 3

- `matches`
- `startsWith`
- `endsWith`
- `oneOf`
- `notOneOf`
- `empty`
- `notEmpty`

### Phase 4

- `typeIs`
- `sizeEquals`
- `sizeGreaterThan`
- `sizeLessThan`

The exact release in which each operator is introduced may change.