package io.jexxa.jlegmed.plugins.persistence.jdbc;

import io.jexxa.adapterapi.ConfigurationFailedException;
import io.jexxa.jlegmed.core.JLegMed;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static io.jexxa.jlegmed.plugins.generic.producer.StreamProducer.streamProducer;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JDBCSessionPoolIT {
    private static JLegMed jLegMed;

    @BeforeEach
    void init() {
        jLegMed = new JLegMed(JDBCFlowGraphsIT.class)
                .useTechnology(JDBCSessionPool.class)
                .disableBanner();
    }

    @AfterEach
    void deInit() {
        jLegMed.stop();
    }

    @Test
    void failFastInvalidProperties() {
        //Arrange
        var messageCollector = new Stack<DataToBeStored>();
        var database = new JDBCStatements();
        var testData = Stream.of(new DataToBeStored(1, "Hello"));

        jLegMed.newFlowGraph("HelloWorld")

                .await(DataToBeStored.class)
                .from(() -> streamProducer(testData))
                .and().processWith(database::insert).useProperties("invalid-pw-jdbc-connection")
                .and().consumeWith(messageCollector::push);
        //Act/Assert
        assertThrows(ConfigurationFailedException.class, () -> jLegMed.start());
        assertTrue(messageCollector.empty()); // Only to avoid warning that messageCollector is not used
    }

    @Test
    void disabledFailFastInvalidProperties() {
        //Arrange
        jLegMed.disableStrictFailFast();
        var messageCollector = new Stack<DataToBeStored>();
        var errorCollector = new Stack<DataToBeStored>();
        var testData = Stream.of(new DataToBeStored(1, "Hello"));
        var database = new JDBCStatements();

        jLegMed.newFlowGraph("HelloWorld")

                .await(DataToBeStored.class)
                .from(() -> streamProducer(testData)).onError(processingError -> errorCollector.push(processingError.originalMessage()))
                .and().processWith(database::insert).useProperties("invalid-pw-jdbc-connection")
                .and().consumeWith(messageCollector::push);
        //Act/Assert
        jLegMed.start();
        await().atMost(1, TimeUnit.SECONDS).until(() -> !errorCollector.isEmpty());
        assertTrue(messageCollector.empty()); // Only to avoid warning that messageCollector is not used
    }

}
