# 3. Functional interface processor 

Date: 2024-01-22

## Status

Accepted

## Context

When defining a flow graph, we should:
  * Separate its logic as well as possible to avoid dependencies between filters 
  * Focus on `what` instead of `how` to improve readability and maintenance of the application. 

## Decision

When defining a flow graph, we should define only the combination of functional statements using functional interfaces, at least for processors. 

## Consequences
* Functional interface might need access to state information provided in `FilterContext` and `Properties`
* Functional interface might need access to the output pipe for proper and efficient data forwarding  
* Functional interfaces inherit Serializable, to show the method name of non-anonymous functions for logging and error handling in production.     
* The Processor API as well as the FlowGraph Builder API must offer suitable methods for typical functional interfaces. In case we have to add a new functional interface, we must extend the core API 
  * Current functional interfaces are: 
    * `Consumer`, `BiConsumer` for consuming data and data plus `FilterContext`
    * `Function`, `BiFunction` for processing data and data plus `FilterContext`
    * `PipedProcessor` in case control over output pipe is required 