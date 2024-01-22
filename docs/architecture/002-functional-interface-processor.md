# 2 . Functional interface processor 

Date: 2024-01-22

## Status

Proposed

## Context

When defining a flow graph application developer should focus on `what` instead of `how` to improved readability and 
maintenance of the application. 


## Decision

Processor filter should provide a functional interface to call a specific operation for a technology stack. 

## Consequences

* To ensure a fail fast approach (adr xxx) technology stacks cannot depend on the `init` method of a processor. For example, a JDBC or JMS processor should try to init a connection. Proposal: A `Manager` of the technology stack is informed about the properties of the JLegMedInstance, reads its known properties and tries to initialize the technology stack for each property. -> All information about the technology stack must be written im the properties file  
* The Processor API as well as the FlowGraph Builder API must offer suitable methods for typical functional interfaces. In case we have to add a new functional interface, we must extend the core API 
  * Typical functional interfaces are: 
    * Consumer, BiConsumer
    * 