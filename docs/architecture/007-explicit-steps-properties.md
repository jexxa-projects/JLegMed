# ADR: Explicitly Configured Steps for Property Resolution

## Status
Accepted

## Context

JLegMed provides a DSL for defining explicit data flows. Each flow consists of
a sequence of processing steps such as `streamWith`, `processWith`, and `sinkTo`.

Some steps require configuration via external properties (e.g. `PersistentTimer`).
Originally, these properties could be resolved implicitly based on naming
conventions (e.g. mapping `PersistentTimer` to a `persistenttimer` section in
a configuration file).

While this convention reduces boilerplate, it introduces several challenges:

- The use of properties is not visible in the DSL
- Behavior depends on external configuration without explicit indication
- Refactoring (e.g. renaming classes) may silently break configuration
- Developers cannot easily discover configuration by navigating the code in the IDE
- Violates the otherwise explicit nature of the JLegMed DSL

At the same time, requiring explicit configuration for every step increases
boilerplate and reduces readability.

## Decision

We introduce an explicit `Step` abstraction that encapsulates both the
processing function and its configuration.

Two variants of step creation are supported:

1. **Explicit configuration**

```java
step(PersistentTimer::nextInterval)
    .withProperties("persistenttimer")
```

2. **Convention-based configuration (explicitly marked)**

```java
configuredStep(PersistentTimer::nextInterval)
```

The `configuredStep` variant applies the existing naming convention for
property resolution but makes this behavior explicit in the DSL.

Additionally, developers are encouraged to assign steps to well-named variables:

```java
var naechstesIntervall =
    configuredStep(PersistentTimer::nextInterval);

.then().streamWith(naechstesIntervall)
```

This enables:

- IDE navigation to the full configuration
- reuse of configured steps
- improved readability through domain-specific naming

The DSL continues to support direct method references for simple cases:

```java
.streamWith(PersistentTimer::nextInterval)
```

However, this form should only be used for steps that do not require configuration.

## Consequences

### Positive

- Makes configuration explicit and visible in the DSL
- Preserves readability by avoiding excessive boilerplate
- Eliminates hidden dependencies on naming conventions
- Improves IDE support (navigation, discoverability)
- Enables reuse and domain-specific naming of steps
- Keeps backward compatibility with existing DSL usage

### Negative

- Introduces an additional abstraction (`Step`)
- Requires developers to learn when to use `step` vs `configuredStep`
- Slightly increases API surface

### Neutral

- Naming conventions for property resolution are still supported,
  but no longer implicitly applied without indication

## Alternatives Considered

### Implicit property resolution (status quo)

Properties are automatically resolved based on naming conventions.

Rejected because:
- behavior is not visible in the DSL
- difficult to debug and maintain
- contradicts explicit design goals

---

### Always require explicit `.useProperties(...)`

```java
.streamWith(PersistentTimer::nextInterval)
.useProperties("persistenttimer")
```

Rejected because:
- adds boilerplate
- reduces readability of flows
- clutters simple use cases

---

### Global automatic property binding

Properties are always applied if available.

Rejected because:
- introduces hidden behavior
- makes flows harder to reason about
- increases risk of unintended side effects
