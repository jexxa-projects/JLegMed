# 3. Functional Interface Processor

**Date:** 2024-01-22  
**Extended:** 2025-10-16  
**Status:** Accepted

## Context
When defining a flow graph, the goal is to:
* Achieve strong separation of logic to minimize dependencies between filters.
* Emphasize **what** the processing does, rather than **how** it is implemented, to improve readability, reusability, and maintainability.

## Decision
Flow graph definitions—specifically for processors—should rely solely on **combinations of functional statements using functional interfaces**.  
This ensures a clear and declarative representation of the processing logic, consistent with functional programming principles.

In addition, all functional interfaces should be implemented as **static methods** to ensure they are **stateless**—that is, functions in the mathematical sense, without side effects or hidden state dependencies.

## Consequences
* Functional interfaces may require access to state information provided by `FilterContext` and `Properties`.
* Functional interfaces may also need access to the output pipe to enable efficient data forwarding.
* All functional interfaces must implement `Serializable` to facilitate logging and error handling in production, allowing non-anonymous method names to be displayed.
* The Processor API and FlowGraph Builder API must expose appropriate methods for common functional interfaces. Whenever a new functional interface type is introduced, the core API must be extended accordingly.
* Enforcing stateless, static implementations promotes predictability, thread-safety, and ease of testing.

## Rationale
Using **static, stateless functional interfaces** ensures that processors behave as pure functions:
* They produce the same output for the same input, independent of external state.
* They can be executed safely in parallel and across distributed environments.
* They are easier to reason about, test, and maintain.
* This approach aligns with the principles of functional programming and supports clear, composable, and deterministic flow graph definitions.

### Currently Supported Functional Interfaces
* **`Consumer` / `BiConsumer`** – for consuming data, or data along with a `FilterContext`.
* **`Function` / `BiFunction`** – for transforming data, or data along with a `FilterContext`.
* **`PipedProcessor`** – for scenarios requiring explicit control over the output pipe.  
