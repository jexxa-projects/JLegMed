[![Maven Test Build](https://github.com/jexxa-projects/JLegMed/actions/workflows/mavenBuild.yml/badge.svg)](https://github.com/jexxa-projects/JLegMed/actions/workflows/mavenBuild.yml)
[![New Release](https://github.com/jexxa-projects/JLegMed/actions/workflows/newRelease.yml/badge.svg)](https://github.com/jexxa-projects/JLegMed/actions/workflows/newRelease.yml)

# JLegMed
This library is intended to build bridges between legacy and new systems. Goal is to ensure that a new system can use a greenfield approach without considering the behavior or technical aspects of a legacy system.  

For this purpose, we focus on the following aspects:  
* Simplify translating between different technology stacks
* Simplify mapping between data sets 
* Make the data flow and transformation visible in the source code 


## Requirements

*   Java 17+ installed
*   IDE with maven support 

## Example 

Refer to the following tests to get an idea how this library works
* [Manual flow graph setup](src/test/java/io/jexxa/jlegmed/core/ManualFlowgraphTest.java) 
* [Scheduled processing](src/test/java/io/jexxa/jlegmed/core/FlowGraphBuilderTest.java) using FlowGraphBuilder
* [Async processing](src/test/java/io/jexxa/jlegmed/plugins/messaging/MessageReceiverIT.java) using FlowGraphBuilder


## Build the library

*   Checkout the new project in your favorite IDE

*   Without running integration tests:
    ```shell
    mvn clean install 
    ```


