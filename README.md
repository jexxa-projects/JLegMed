[![Maven Central](https://img.shields.io/maven-central/v/io.jexxa.jlegmed/jlegmed-core)](https://maven-badges.herokuapp.com/maven-central/io.jexxa.jlegmed/jlegmed-core/) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

[![Maven Build](https://github.com/jexxa-projects/JLegMed/actions/workflows/mavenBuild.yml/badge.svg)](https://github.com/jexxa-projects/JLegMed/actions/workflows/mavenBuild.yml)
[![CodeQL](https://github.com/jexxa-projects/JLegMed/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/jexxa-projects/JLegMed/actions/workflows/codeql-analysis.yml)
# 🧩 JLegMed — Connect the Past with the Future

**JLegMed** is a lightweight Java library for building **explicit technical data flows** between systems. 
It is designed to isolate **integration, translation, and reconstruction logic** from business applications and domain models.

JLegMed focuses on *how data flows and is transformed*, not on *what the data means to the business*.


## 🧭 When to Use JLegMed

If you are dealing with integration logic that feels harder than it should be,
JLegMed is worth experimenting with.

Typical signals include:
- translation logic keeps growing but does not belong to the domain
- CDC or polling delivers facts, but their meaning remains unclear
- integration code becomes procedural, stateful, and hard to test
- declarative mappings stop being sufficient

If any of these resonate, starting with a small experiment is usually
low-effort and highly informative.


## 🚀 Features

- **Pipes & Filters Architecture**  
  A natural fit for explicit, step-by-step data-flow processing

- **Functional Programming Style**  
  Encourages clear transformation logic and testable processing steps

- **Explicit State Handling**  
  Makes buffering, deduplication, and reconstruction logic visible and controllable

- **Minimal Dependencies**  
  Built on Java SE APIs, easy to embed and reason about

- **Easy Integration**  
  Works well with JMS, JDBC, TCP, Kafka, S3, and similar technologies

## 📋 Requirements

- Java **25 or higher**
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
        <version>5.1.3</version>
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
compile "io.jexxa:jlegmed-core:5.1.3"
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

