# 3. Functional interface processor 

Date: 2024-01-22

## Status

Proposed

## Context

When defining a flow graph, we should:
  * Separate its logic as well as possible to avoid dependencies between filters 
  * Focus on `what` instead of `how` to improve readability and maintenance of the application. 

## Decision

All filters should provide a functional interface to hand in a specific functionality. 

## Consequences
* Functional interface might need access to the `FilterContext` which provides `Properties` and state information
* Functional interface might need access to the output pipe for proper and efficient data forwarding  

* The Processor API as well as the FlowGraph Builder API must offer suitable methods for typical functional interfaces. In case we have to add a new functional interface, we must extend the core API 
  * Current functional interfaces are: 
    * `Consumer`, `BiConsumer` for consuming data (optional with FilterContext)
    * `Function`, BiFunction for processing data (optional with FilterContext)
    * `PipedProcessor` in case control over output pipe is required 