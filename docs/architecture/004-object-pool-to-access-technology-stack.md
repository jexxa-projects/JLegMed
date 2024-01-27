# 4. Object Pool to access technology stack 

Date: 2024-01-26

## Status

Accepted

## Context

Since we would like to use lambda expressions in filters, we need a way to initialize, reuse and free technology related aspects such as a file handle, or a connection.  

## Decision

We use an `object pool` to manage the caching of the technology-related objects. A lambda function can access the object pool and avoid creating a new object by simply asking the pool for one that has already been instantiated instead. For identifying the required object, 
we use the `useProperties` name defined in flow graph

## Consequences

* We must provide a uniform way to pass `Properties` information to the `object pool` of a specific technology stack so that it can be validated before assuming that the flowgraph is running 