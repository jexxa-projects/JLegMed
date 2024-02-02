package io.jexxa.jlegmed.plugins.persistence.jdbc;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.processor.GenericCollector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static io.jexxa.jlegmed.plugins.generic.producer.StreamProducer.streamProducer;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        var messageCollector = new GenericCollector<DataToBeStored>();
        var database = new JDBCStatements();

        jLegMed.newFlowGraph("HelloWorld")

                .await(DataToBeStored.class)
                .from(streamProducer(Stream.empty()))
                .and().processWith(database::insert).useProperties("invalid-pw-jdbc-connection")
                .and().consumeWith(messageCollector::collect);
        //Act/Assert
        assertThrows(IllegalArgumentException.class, () -> jLegMed.start());
    }
}
