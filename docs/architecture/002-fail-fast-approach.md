# 2. Fail Fast approach

Date: 2024-01-18

## Status

Accepted

## Context

A flowgraph can start its processing after an arbitrary time after the application was started. We need a way to ensure that a flowgraph, its configuration and required infrastructure are available in principle to minimize runtime error. 

## Decision

We implement a fail-fast approach to ensure that the configuration of a flowgraph is valid and technology stacks are available at startup.

## Consequences

* When using a lambda expression in filters that requires access to a technology stack, this property information must be defined by using method `useProperties`.
* To detect if a developer forgets to add this method, we assume that a lambda expression using `FilterContext` as parameter requires `useProperties` until it is explicitly negated with `noProperties
* JLegMed passes all `Properties` information to initialized `object pools` so that they can validate their properties
