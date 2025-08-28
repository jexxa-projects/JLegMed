# 4. Object Pool for Accessing the Technology Stack

**Date:** 2024-01-26  
**Status:** Accepted

## Context
Technology stacks often rely on stateful information such as file handles, session handles, or connections.  
Since we encourage the use of lambda expressions in **JLegMed**, we need a mechanism to initialize and manage such state information **outside** of the lambda expression.

## Decision
We introduce a dedicated **resource pool** for each specific technology stack.  
This pool manages the lifecycle of stateful resources at the filter level.

A lambda expression, encapsulated within a filter, can then access the resource pool and reuse an already instantiated resource instead of creating a new one.  
The required resource is identified using the `useProperties` name defined in the flow graph.

## Consequences
* The `FilterContext` of a filter is used so that the resource pool can manage resources at the filter level.
* All configuration aspects of a technology stack or a specific connection must be defined using **properties**. If a developer forgets to configure these properties, [ADR 002 - Fail-Fast Approach](004-fail-fast-approach.md) ensures the application fails fast.
* Resource pools must be implemented as **singletons** to be reusable in lambda expressions. For proper initialization of these singletons in Java, the class information must be passed to JLegMed in the main method so that it can enforce class loading.
* Due to the [ADR 002 - Fail-Fast Approach](004-fail-fast-approach.md), all resource pools receive the full set of `Properties`. They must therefore be able to detect and ignore properties that are not relevant to themselves.  