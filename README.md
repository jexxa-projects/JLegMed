[![Maven Central](https://img.shields.io/maven-central/v/io.jexxa.jlegmed/jlegmed-core)](https://maven-badges.herokuapp.com/maven-central/io.jexxa.jlegmed/jlegmed-core/) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

[![Maven Build](https://github.com/jexxa-projects/JLegMed/actions/workflows/mavenBuild.yml/badge.svg)](https://github.com/jexxa-projects/JLegMed/actions/workflows/mavenBuild.yml)
[![CodeQL](https://github.com/jexxa-projects/JLegMed/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/jexxa-projects/JLegMed/actions/workflows/codeql-analysis.yml)
# JLegMed—Connect the Past with the Future

This library is intended to build bridges between legacy and new systems. Goal is to ensure that a new system can use a greenfield approach without considering the behavior or technical aspects of a legacy system.  

For this purpose, we focus on the following aspects:  
* Simplify translating between different technology stacks
* Simplify mapping between data sets 
* Make the data flow and transformation visible in the source code 


## Requirements

*   Java 17+ installed
*   IDE with maven support 


## Quickstart

### Start programming

Below, you see a simple ``Hello World`` example:

```java     
public final class HelloJLegMed
{
    public static void main(String[] args)    {
        var jLegMed = new JLegMed(HelloJLegMed.class);

        jLegMed.newFlowGraph("HelloWorld")
                .every(1, TimeUnit.SECONDS)

                .receive(String.class).from( () -> "Hello " )
                .and().processWith(data -> data + "World" )
                .and().consumeWith(data -> getLogger(JLegMedTest.class).info(data));

        jLegMed.run();
    }
}
```    

### Add Dependencies
Whenever possible, JLegMed is developed against standard APIs. This allows an application to use the preferred
technology stacks. Therefore, our application needs two dependencies: `jlegmed` and a logger that fulfills
your requirements, such as `slf4j-simple`.

Maven:
```xml
<dependencies>
    <dependency>
        <groupId>io.jexxa.jlegmed</groupId>
        <artifactId>jlegmed-core</artifactId>
        <version>0.7.0</version>
    </dependency>
    
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-simple</artifactId>
        <version>2.0.9</version>
    </dependency>
</dependencies>
```

Gradle:

```groovy
compile "io.jexxa:jlegmed-core:0.7.0"
compile "org.slf4j:slf4j-simple:2.0.9"
``` 

## Example 

Refer to the following tests to get an idea how this library works
* Typical use cases using `HelloWorld` as an example:
  * [Data processing with a fixed number of iterations (e.g., for testing)](jlegmed-core/src/test/java/io/jexxa/jlegmed/core/flowgraph/RepeatFlowGraphTest.java)
  * [Data processing at a specific interval](jlegmed-core/src/test/java/io/jexxa/jlegmed/core/flowgraph/ReceiveFlowGraphTest.java) 
  * [Await data for processing](jlegmed-core/src/test/java/io/jexxa/jlegmed/core/flowgraph/AwaitFlowGraphTest.java)

* [Configuration of a flowgraph](jlegmed-core/src/test/java/io/jexxa/jlegmed/core/flowgraph/FlowGraphBuilderConfigurationTest.java) using `FlowGraphBuilder`

## Build the library

*   Checkout the new project in your favorite IDE

*   Without running integration tests:
    ```shell
    mvn clean install 
    ```
    
## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## Copyright and license

Code and documentation copyright 2023 Michael Repplinger. Code released under the [Apache 2.0 License](LICENSE). Docs released under [Creative Commons](https://creativecommons.org/licenses/by/3.0/).
