package io.jexxa.jlegmed.plugins.persistence.jdbc;

import io.jexxa.jlegmed.core.JLegMed;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;
import java.util.stream.Stream;

import static io.jexxa.jlegmed.plugins.generic.producer.StreamProducer.streamProducer;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JDBCSessionPoolIT {
    private static JLegMed jLegMed;

    @BeforeEach
    void init() {
        jLegMed = new JLegMed(JDBCFlowGraphsIT.class).disableBanner();
    }

    @AfterEach
    void deInit() {
        jLegMed.stop();
    }

    @Test
    void failFastInvalidProperties() {
        //Arrange
        JDBCSessionPool.init();
        var messageCollector = new Stack<DataToBeStored>();
        var database = new JDBCStatements();

        jLegMed.newFlowGraph("HelloWorld")

                .await(DataToBeStored.class)
                .from(() -> streamProducer(Stream.empty()))
                .and().processWith(database::insert).useProperties("invalid-pw-jdbc-connection")
                .and().consumeWith(messageCollector::push);
        //Act/Assert
        assertThrows(IllegalArgumentException.class, () -> jLegMed.start());
        assertTrue(messageCollector.empty()); // Only to avoid warning that messageCollector is not used
    }
}
