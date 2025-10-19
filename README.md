[![Maven Central](https://img.shields.io/maven-central/v/io.jexxa.jlegmed/jlegmed-core)](https://maven-badges.herokuapp.com/maven-central/io.jexxa.jlegmed/jlegmed-core/) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

[![Maven Build](https://github.com/jexxa-projects/JLegMed/actions/workflows/mavenBuild.yml/badge.svg)](https://github.com/jexxa-projects/JLegMed/actions/workflows/mavenBuild.yml)
[![CodeQL](https://github.com/jexxa-projects/JLegMed/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/jexxa-projects/JLegMed/actions/workflows/codeql-analysis.yml)
# 🧩JLegMed—Connect the Past with the Future
**JLegMed** is a lightweight Java framework that combines the principles of domain-driven design (DDD) with seamless integration between traditional enterprise systems and modern cloud-native platforms.
It connects JMS-, JDBC-, and TCP-based business applications with Kafka and S3 to support both transaction- and event-driven workflows.

By aligning clear domain boundaries with flexible technical adapters, JLegMed helps you modernize existing systems step by step to establish coherent DDD-based business logic and operate it in an on-premise, cloud-native, or hybrid infrastructure.

## 🚀 Features

- **Pipes & Filters Architecture** – A natural fit for data-flow-based processing
- **Functional Programming Style** – Focus on the *what*, not the *how*
- **Minimal Dependencies** – Built on Java SE APIs
- **Easy Integration** – Compatible with JMS, JDBC, TCP, Kafka, S3, and more

## 📋 Requirements

- Java **17 or higher**
- Maven-compatible IDE (e.g., IntelliJ IDEA, Eclipse)

## 🛠️ Quickstart
### Hello World Example
```java     
public final class HelloJLegMed
{
 static void main(String[] args)    {
        var jLegMed = new JLegMed(HelloJLegMed.class);

        jLegMed.newFlowGraph("HelloWorld")
                .every(1, TimeUnit.SECONDS)

                .receive(String.class).from( () -> "Hello " )
                .and().processWith(data -> data + "World" )
                .and().consumeWith(System.out::println);

        jLegMed.run();
    }
}
```

### Add Dependencies

Maven:
```xml
<dependencies>
    <dependency>
        <groupId>io.jexxa.jlegmed</groupId>
        <artifactId>jlegmed-core</artifactId>
        <version>4.5.0</version>
    </dependency>
    
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-simple</artifactId>
        <version>2.0.17</version>
    </dependency>
</dependencies>
```

Gradle:

```groovy
compile "io.jexxa:jlegmed-core:4.5.0"
compile "org.slf4j:slf4j-simple:2.0.17"
``` 
## 📚 Use Cases with Examples

### Basic Patterns
* [Fixed iteration count](jlegmed-core/src/test/java/io/jexxa/jlegmed/examples/RepeatFlowGraphTest.java)
* [Time-triggered execution](jlegmed-core/src/test/java/io/jexxa/jlegmed/examples/ReceiveFlowGraphTest.java)
* [Wait-for-data triggers](jlegmed-core/src/test/java/io/jexxa/jlegmed/examples/AwaitFlowGraphTest.java)

### Advanced Patterns
* [Flow graph configuration](jlegmed-core/src/test/java/io/jexxa/jlegmed/examples/FlowGraphConfigurationTest.java)
* [Flow graph introspection](jlegmed-core/src/test/java/io/jexxa/jlegmed/plugins/monitor/FlowGraphMonitorTest.java)
* [Data fan-out / fan-in (multi-sinks/sources)](jlegmed-core/src/test/java/io/jexxa/jlegmed/plugins/generic/muxer/ThreadedMultiplexerTest.java)
* [Custom error handling strategies](jlegmed-core/src/test/java/io/jexxa/jlegmed/examples/ErrorHandlingTest.java)
* [Bootstrapping and flow control](jlegmed-core/src/test/java/io/jexxa/jlegmed/examples/BootstrappingFlowGraphTest.java)

### Support for sending/receiving over:
* [JMS (e.g., ActiveMQ)](jlegmed-core/src/test/java/io/jexxa/jlegmed/plugins/messaging/jms/MessagingTestIT.java)
* [TCP Sockets](jlegmed-core/src/test/java/io/jexxa/jlegmed/plugins/messaging/tcp/TCPMessagingIT.java)
* [JDBC (e.g., PostgreSQL)](jlegmed-core/src/test/java/io/jexxa/jlegmed/plugins/persistence/jdbc/JDBCFlowGraphsIT.java)

## 🌐 Ecosystem
* [Pre-built template](https://github.com/jexxa-projects/JexxaArchetypes) for quick project startup
* Fully Maven-compatible
* Optional integration with the [Jexxa Framework](https://www.jexxa.io)


## 🤝 Contributing

Contributions are welcome!
For large changes, please open an issue to discuss what you have in mind.

Please make sure:
* All code changes are covered by tests
* Documentation is updated accordingly

## 📜 License
* Source code: [Apache 2.0 License](LICENSE) - see [TLDR legal](https://tldrlegal.com/license/apache-license-2.0-(apache-2.0))
* Documentation: [Creative Commons](https://creativecommons.org/licenses/by/4.0/)
* ©️ 2023–2025 Michael Repplinger

