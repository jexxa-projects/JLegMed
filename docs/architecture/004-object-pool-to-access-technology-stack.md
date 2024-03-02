# 4. Object Pool to access technology stack 

Date: 2024-01-26

## Status

Accepted

## Context
Technology stacks typically depend on state related information such as a file handle or a connection.
Since we define our processing part of the flow graph using lambda expressions, we need a way to initialize,
reuse these technology stacks outside the lambda expression at least for processing filters.  

## Decision

We use an `object pool` to manage technology-related states.
A lambda expression can access the object pool
and avoid creating a new object by simply asking the pool for one that has already been instantiated instead.
For identifying the required object, 
we use the `useProperties` name defined in flow graph

## Consequences

* All configuration aspects of a technology stacks or a specific connection must be defined using properties. In case a developer forgets to configure the properties [ADR 002-fail-fast-approach](002-fail-fast-approach.md) handles this failure. 
* Object pools must be implemented as singleton to be used in lambda expressions. For proper initialization of these singletons in Java, the class information must be passed to JLegMed in the main method so that it can force class loading   
* Due to [ADR 002-fail-fast-approach](002-fail-fast-approach.md), object pools get `Properties` information for all pools. So they must be able to detect and ignore properties that are not related to themselves.        
