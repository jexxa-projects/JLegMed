# 5. Error handling during message processing

Date: 2024-02-18

## Status

Accepted

## Context
When processing messages, arbitrary errors and failures can occur. We need a unified concept for error handling to support the developer.   

In this context, we distinguish between two kinds of errors:
* An exception in terms of an __abnormal situation__ such as the used database is no longer available for some reason. In this case, no processing can be performed and previous stateful changes must be rolled back. 
* An exception in terms of an __expected misbehavior__ such as a service is temporarily not available. In this case, we have to execute different processing steps.    


## Decision
* To handle an __abnormal situation__, we use the `transactional outbox pattern` to avoid complexity of two phases commits.
* To handle an __expected misbehavior__, we use the concept of a monad for a binding, which allows to define a processor or processing flow graph for error handling after each binding.  


## Consequences

* Due to transaction outbox pattern, all technologie stacks must first write their information to the same database first. Due to this consequence, a single flow graph should not update different databases, for example. If such a scenario is desired, we recommend not using this framework.           
* For handling an __expected misbehavior__ that requires multiple processing steps, a separate flow graph must be defined. This causes that a strict ordering of messages is lost. If strict message ordering is required, this must be considered by the flow graph and/or application (e.g. by stopping processing).  
* Since handling of an __expected misbehavior__ can be caused by a succeeding filter, but only a previous filter provides error handling, we introduce a `ProcessingException` so that the previous filter is aware that he has to forward this information to the error pipe.   
