# Change Log
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## \[0.8.0] - 2023-11-19 (RC for first major release)
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
- JLegMed provides a run method which waits until application is terminated in some way

### Changed
- Streamlined entire API so that this version is not compatible with 0.2.0  

## \[0.2.0] - 2023-11-01

### Added
- Initial version that is tracked in ChangeLog