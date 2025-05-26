# 4. Object Pool to access technology stack 

Date: 2024-01-26

## Status

Accepted

## Context
Technology stacks typically depend on state-related information such as a file or session handle.
Since we are pushing the use of lambda expressions, we need a way to initialize and
manage this technology-related state information outside the lambda expression.  

## Decision

We use a dedicated `ressource pool` for a specific technology stack to manage it states on a filter level.
A lambda expression, encapsulated in a filter, can then access the ressource pool
and avoid creating a new ressource by simply asking the pool for one that has already been instantiated instead.
For identifying the required ressource, 
we use the `useProperties` name defined in flow graph

## Consequences
* We use the `FilterContext` of a filter so that a resource pool can manage resources on a filter level. 
* All configuration aspects of a technology stack or a specific connection must be defined using properties. In case a developer forgets to configure the properties, [ADR 002-fail-fast-approach](004-fail-fast-approach.md) handles this failure. 
* Resource pools must be implemented as singletons to be used in lambda expressions. For proper initialization of these singletons in Java, the class information must be passed to JLegMed in the main method so that it can force class loading   
* Due to [ADR 002-fail-fast-approach](004-fail-fast-approach.md), ressource pools get `Properties` information for all pools. So they must be able to detect and ignore properties that are not related to themselves.        
