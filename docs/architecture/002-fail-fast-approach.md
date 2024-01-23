# 2. Fail Fast approach

Date: 2024-01-18

## Status

Accepted

## Context

A flowgraph can start its processing after an arbitrary time after the application was started. We need a way to ensure 
that a flowgraph, its configuration and required infrastructure are available in principle to minimize runtime error. 

## Decision

We implement a fail-fast approach to ensure that the configuration of a flowgraph is valid and technology stacks are available at startup

## Consequences

* When implementing a specific filter, we provide an `init` method that is used to validate the configuration
* When using a lambda expression in filters that requires access to a technology stack, this property information must be handed 
using method `useProperties`. 
* We must provide a uniform way to pass `Properties` information to the `Manager` of a specific technology stack so that it can be validated before assuming that the flowgraph is running 