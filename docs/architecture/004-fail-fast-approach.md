# 2. Fail Fast approach

**Date:** 2024-01-18  
**Extended:** 2025-07-25  
**Status:** Accepted

## Status

Accepted

## Context

Flowgraphs may start processing at any time after the application has started.
To avoid late runtime failures, we must ensure that flowgraphs, their configurations, and dependent infrastructure are fundamentally available and valid at startup.

## Decision

We enforce a fail-fast approach: during application startup, flowgraph configurations are validated, and required technology components (e.g., Kafka topics, DB connections) are checked for availability. If any of these validations fail, the application fails to start.

## Consequences
* Misconfigurations or missing infrastructure will lead to early failure, preventing unpredictable runtime errors.
* Application startup time may slightly increase due to early validations.
* Developers need to mock or provide infrastructure for tests that trigger startup checks.

* Lambda-expressions: 
  * When using a lambda expression in filters that requires access to a technology stack, this property information must be defined by using method `useProperties`.
  * To detect if a developer forgets to add this method, we assume that a lambda expression using `FilterContext` as parameter requires `useProperties` until it is explicitly negated with `noProperties
  * JLegMed passes all `Properties` information to initialized `object pools` so that they can validate their properties
  * __Extension on 2025-07-24__: 
    Using method `useProperties` can cause a lot of boilerplate code, for example, if the application reads different semantic information from the same source such as DB. This happens especially, when passing functional methods. To avoid this, we introduce the following convention: 
       * By default, we check the properties if a properties-prefix exists that matches the class name. 
       * If it exists, we pass it as properties to the filter. 
       * If a properties definition is explicitly defined by using `useProperties` this one is used