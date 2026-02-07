# Change Log
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## \[5.1.3] - 2026-02-07
### Fixed
- Updated dependencies 

## \[5.1.2] - 2026-01-01
### Fixed
- Corrected architecture rules in case of empty plugins  

## \[5.1.1] - 2025-12-20
### Fixed
- Fail fast for repositories first check if an explicit strategy is defined. If so, defined properties are only validated if they match to the specified strategy.  

## \[5.1.0] - 2025-12-17
### Added
- TimeUtils filter to add a look ahead or subtract a look back window  

### Fix
- S3 Repositories are now validated for fail fast 
- Updated dependencies 

## \[5.0.0] - 2025-11-08
### Changed
- JLegMed requires now Java25+
- Removed deprecated methods/classes 

### Fixed
- Updated Dependencies


## \[4.5.0] - 2025-10-19
### Added
- Repository and object store now support S3-storage (introduced with the update to common-adapters 2.6.0)

### Fixed
- Updated Dependencies

## \[4.4.0] - 2025-09-14
### Added
- Plugin `PersistentTimer` now supports properties `lookback.period` to define a relative start time in seconds (s), minutes(m), hours(h), days(d), and weeks(w). 
  - Example: `lookback.period = 5d` for an initial start time of now() - 5 days  

## \[4.3.0] - 2025-09-06
### Changed
- BootstrapRegistry is declared as deprecated and replaced by JexxaContext  

### Fixed
- Updated Dependencies

## [4.2.0] - 2025-08-04
### Added
- Added default method `value()` to FlowGraph annotation

### Changed
- Renamed method `nextInterval` to `nextIntervalWithConfig` in PersistentTimer to avoid type erasure issues
- Limited DTO architecture rules to public classes only

### Deprecated
- Deprecated `name()` method in FlowGraph annotation in favor of `value()`

## \[4.1.2] - 2025-07-29
### Fixed
- JDBCConnection management uses FilterContext to ensure unique JDBCConnection
- RepositoryPool access methods are now synchronized to avoid ConcurrentModificationException
- RepositoryPool management uses FilterContext to ensure unique Repository instances


## \[4.1.1] - 2025-07-27
### Fixed
- Architecture rules for DTOs ignore nested classes correctly 

## \[4.1.0] - 2025-07-26
### Added 
- Added annotation `ConsumedMessage` for incoming messages

### Fixed
- Introduced annotations
  - Annotations that are used with classes not for methods 
  - Architecture rules to ignore nested classes
- Fixed producing filter to define a parent class that is used for the default prefix 
- The default message sender is `JMSSender` and not `TransactionalOutboxSender`


## \[4.0.0] - 2025-07-25
### Added 
- JLegMedTest to simplify testing applications based on jlegmed
- Annotations for architecture tests
- New plugin PersistentTimer 
- Architecture tests

### Fixed 
- Updated dependencies

### Changed
- Removed all deprecated methods and classes
- Filter name for functional methods is now based on `ClassName::methodName` which is more expressive



## \[3.5.8] - 2025-07-07
### Fixed
- Updated dependencies
- Fix: create a deep copy of properties set in `FilterProperties`. Otherwise, the internal properties could be overwritten if reused in other `FilterProperties`

## \[3.5.7] - 2025-06-25
### Fixed
- Updated dependencies
- Correct cleanup of JLegMed application if it is terminated with siq-quit

## \[3.5.6] - 2025-05-26
### Fixed
- Updated dependencies
- Moved the deployment process from the legacy OSS deploy server to the new central sonatype

## \[3.5.5] - 2025-04-15
## Fix
- Updated dependencies

## \[3.5.4] - 2025-03-18
## Fix
- Updated dependencies

## \[3.5.3] - 2025-02-04
## Fix
- Updated dependencies

## \[3.5.2] - 2025-02-04
## Fix
- Updated dependencies

## \[3.5.1] - 2024-11-22
## Fix
- Fixed race condition when accessing ressource pools 
- Updated dependencies

## \[3.5.0] - 2024-11-11
### Added
- New SynchronizedMultiplexer that multiplexes messages if a single message is received on each input. As soon as a message is received on an input. This input blocks and waits until a message is received on the other input or a defined timeout occurs 
- New ThreadedMultiplexer (formerly BiFunctionMultiplexer) that receives as many messages as possible without blocking the input
- Added possibility to define JLEGMED_CONFIG_IMPORT via environment variable

### Changed
- BiFunctionalMultiplexer is set to deprecated -> Use ThreadedMultiplexer instead

## \[3.4.2] - 2024-11-09
### Fixed
- JMSPool: JMSSession is managed per Filter to ensure that only one thread uses the JMSSession
- TCPConnectionPool: A TCPConnection is managed per Filter to ensure that only one thread uses the TCPConnection 


## \[3.4.1] - 2024-11-01
### Fixed
- A flow graph now waits for the interval defined in `every` after an iteration has been run through 
- Updated dependencies

## \[3.4.0] - 2024-10-15
### Added
- TCPSender: Connection timeout can now be defined  
- Possibility to enable strict transactional behavior of flow graphs. Default mode is that flow graphs can run in parallel

### Fixed
-  Forwarding errors to the succeeding filter is now correctly handled. This fixes the issue that an error is not handled by filters that successfully processed the message.

- Updated dependencies

## \[3.3.0] - 2024-10-05
### Added
- TCPSender can now use different delimiters 

### Fixed
- Updated dependencies
- Improved error messages
- TCPListener could be null, if node is not initialized which is now handled correctly.

## \[3.2.1] - 2024-08-31
### Fixed
- Updated dependencies

## \[3.2.0] - 2024-08-02
### Added
- JMSSource supports now all configuration options of JMS2.x. See examples ind [Receiving and sending data from/to JMS](jlegmed-core/src/test/java/io/jexxa/jlegmed/plugins/messaging/jms/MessagingTestIT.java)

## \[3.1.0] - 2024-08-01
### Added
- Statistics about processed messages and processing errors for each flow graph

### Fixed
- Updated dependencies
- When running multiple flow graphs fail fast approach is first validated on all flow graphs before starting them   
- A processing error is now logged if all error pipes are unconnected  

## \[3.0.4] - 2024-07-03
### Fixed
- Updated dependencies
- Corrected log messages to show causing reason for errors 

## \[3.0.3] - 2024-05-29
### Fixed
- Updated dependencies

## \[3.0.2] - 2024-05-13
### Fixed
- Updated dependencies

## \[3.0.1] - 2024-04-12
### Fixed
- Updated dependencies

## \[3.0.0] - 2024-03-17
### Incompatible Changes
- Updated to jexxa-adapters 2.0.0 which removes deprecated API 
- Renamed `EitherProducer` to `OnErrorProducer` 

## \[2.0.0] - 2024-03-02
### Incompatible Changes
- Implemented a Fail fast approach as described in [ADR002 - Fail Fast approach](docs/architecture/004-fail-fast-approach.md)
- Processor-Filters in FlowGraphBuilder accept only functional statements. See [ADR 003 - Functional interface processor](docs/architecture/003-functional-interface-processor.md) for more information.
- Introduced `Object Pools` that allows processors to use stateful technology stacks. See [ADR 004 - Object Pool to access technology stack ](docs/architecture/005-resource-pool-to-access-technology-stack) for more information.
- Error handling for flow graphs as described in [ADR005 - Error handling during message processing](docs/architecture/006-error-handling-during-message-processing.md)

### Added
- Generic multiplexer in case the result of two flow graphs must be multiplexed. See [here](jlegmed-core/src/test/java/io/jexxa/jlegmed/plugins/generic/muxer/ThreadedMultiplexerTest.java) for an example.

### Fix
- Updated dependencies

## \[2.0.0 - beta 2] - 2024-02-18
### Incompatible Changes
- Implemented a Fail fast approach as described in [ADR002 - Fail Fast approach](docs/architecture/004-fail-fast-approach.md)
- Error handling for flow graphs as described in [ADR005 - Error handling during message processing](docs/architecture/006-error-handling-during-message-processing.md)
### Added 
- Generic multiplexer in case the result of two flow graphs must be multiplexed. See [here](jlegmed-core/src/test/java/io/jexxa/jlegmed/plugins/generic/muxer/ThreadedMultiplexerTest.java) for an example.

### Fix
- Updated dependencies

## \[2.0.0 - beta 1] - 2024-02-04
### Incompatible Changes 
- Start adding ADRs
- Processor-Filters in FlowGraphBuilder accept only functional statements. See [ADR 003 - Functional interface processor](docs/architecture/003-functional-interface-processor.md) for more information.   
- Introduced `Object Pools` that allows processors to use stateful technology stacks. See [ADR 004 - Object Pool to access technology stack ](docs/architecture/005-resource-pool-to-access-technology-stack) for more information.
### Fix
- Updated dependencies


## \[1.0.1] - 2024-01-04
### Fix
- Updated dependencies

## \[1.0.0] - 2023-12-15
- First public release

## \[0.9.0] - 2023-12-05 (RC2 for first major release)
### Changed
- Refactored schedulers so that DrivingAdapters are used

## \[0.8.1] - 2023-11-29 
### Fixed
- Updated dependencies

## \[0.8.0] - 2023-11-19 (RC1 for first major release)
### Changed
- Schedulers are now explicit for filters 
- Cleanup filters 
- Simplified using filter properties 

### Fixed
- Updated dependencies

## \[0.7.0] - 2023-11-16
### Changed
- Added Active/PassiveSource base classes to avoid potential misuse of the API  
- Updated filter accordingly 
 
## \[0.6.0] - 2023-11-12
### Changed
- Fluent API to improve readability and avoid wrong usage
- Restructured packages 
 
### Added
- Dedicated scheduler for flow graphs  

## \[0.5.1] - 2023-11-07
### Fix
- Fixed signing artifacts for central maven

## \[0.5.0] - 2023-11-07
### Added
- New plugin TCPSender to send messages via TCP connection

### Changed 
- To multi-maven project 
- Renamed jar to jlegmed-core as a preparation to separate plugins based on dependencies and technologies

## \[0.4.0] - 2023-11-07
### Added
- FlowGraphBuilder provides methods consumeWith that accept Consumer as an argument
- New plugin TCPReceiver to receive messages via TCP connection 

## \[0.3.0] - 2023-11-05
### Added
- JLegMed provides more sophisticated error handling for uncaught exceptions 
- JLegMed provides a run method which waits until the application is terminated in some way

### Changed
- Streamlined entire API so that this version is not compatible with 0.2.0  

## \[0.2.0] - 2023-11-01

### Added
- Initial version that is tracked in ChangeLog
