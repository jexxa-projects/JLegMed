# 5. Error Handling during Message Processing

**Date:** 2024-02-18  
**Extended:** 2025-08-28  
**Status:** Accepted

## Context
When processing messages, different types of errors and failures may occur. To support developers using **JLegMed**, we need a unified concept for error handling.

We distinguish between two categories of errors:

* **Recoverable errors (expected misbehavior)**  
  Examples: a temporarily unavailable service or invalid/failed data validation. These errors require explicit handling steps, such as notifying the sender about the validation issue.

* **Unrecoverable errors (abnormal situations)**  
  Examples: the database is no longer available or other critical infrastructure failures. Such errors cannot be handled by the current filter itself. Instead, they must be propagated to predecessor filters so they can roll back internal state or apply their own error-handling strategies.

## Decision
* For **recoverable errors**, we use the concept of an *Either monad* within a binding. This allows each processing step to explicitly define an alternative flow for error handling.
* For **unrecoverable errors**, the exception is propagated backwards in the processing graph so that predecessor filters are informed and can perform compensation actions (e.g., rollback) or escalate the failure.

## Consequences
* Handling **recoverable errors** that require multiple processing steps must be modeled as a separate flow graph. For this purpose, we introduce the `onError()` method as part of a binding, which defines a dedicated error-handling flow.
* Since **unrecoverable errors** cannot be resolved within the filter itself, all predecessor filters must be informed. To enable this, we introduce the `ProcessingException`, which signals to predecessor filters that the error must either be forwarded to the error pipeline or be handled by them directly.  