package io.jexxa.jlegmed.plugins.persistence.jdbc;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.GenericProducer;
import io.jexxa.jlegmed.plugins.generic.processor.GenericCollector;
import io.jexxa.jlegmed.plugins.persistence.JDBCStatementsForTestData;
import io.jexxa.jlegmed.plugins.persistence.TestData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class JDBCProcessorIT {
    private static JLegMed jLegMed;

    @BeforeEach
    void init() {
        jLegMed = new JLegMed(JDBCProcessorIT.class).disableBanner();
    }

    @AfterEach
    void deInit() {
        if (jLegMed != null)
        {
            jLegMed.stop();
        }
    }
    @Test
    void writeToDatabase() {
        //Arrange
        var messageCollector = new GenericCollector<TestData>();
        var database = new JDBCStatementsForTestData();

        jLegMed.bootstrapFlowGraph("bootstrap database").execute(database::bootstrapDatabase).useProperties("test-jdbc-connection");

        jLegMed.newFlowGraph("HelloWorld")

                .every(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)

                .and().processWith( data -> new TestData(data, "Hello World " + data))
                .and().processWith( database::insertTestData ).useProperties("test-jdbc-connection")
                .and().consumeWith( messageCollector::collect );
        //Act
        jLegMed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 10);
    }
}
