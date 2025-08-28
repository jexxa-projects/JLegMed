# 3. Functional Interface Processor

**Date:** 2024-01-22  
**Status:** Accepted

## Context
When defining a flow graph, we aim to:
* Separate logic as much as possible to avoid dependencies between filters.
* Focus on **what** the processing should do, rather than **how**, to improve readability and maintainability of the application.

## Decision
When defining a flow graph, only **combinations of functional statements using functional interfaces** should be used, at least for processors. This ensures a clear, declarative definition of the processing logic.

## Consequences
* Functional interfaces may need access to state information provided by the `FilterContext` and `Properties`.
* Functional interfaces may need access to the output pipe to enable proper and efficient data forwarding.
* Functional interfaces inherit `Serializable` to allow logging and error handling in production to display the method name of non-anonymous functions.
* The Processor API and the FlowGraph Builder API must provide suitable methods for common functional interfaces. When adding a new functional interface, the core API must be extended.

Current supported functional interfaces:
* `Consumer` and `BiConsumer` – for consuming data, or data plus `FilterContext`.
* `Function` and `BiFunction` – for processing data, or data plus `FilterContext`.
* `PipedProcessor` – for cases where control over the output pipe is required.  