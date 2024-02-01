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
import static org.junit.jupiter.api.Assertions.assertEquals;

class JDBCProducerIT {
    private static JLegMed jLegMed;

    @BeforeEach
    void init() {
        jLegMed = new JLegMed(JDBCProducerIT.class).disableBanner();
    }

    @AfterEach
    void deInit() {
        if (jLegMed != null)
        {
            jLegMed.stop();
        }
    }
    @Test
    void readFromDatabasePreparedStatements() {
        //Arrange
        var writerCollector = new GenericCollector<TestData>();
        var readerCollector = new GenericCollector<TestData>();

        var jdbc = new JDBCStatementsForTestData();

        // reset the database as part of bootstrapping
        jLegMed.bootstrapFlowGraph("reset database")
                .execute(jdbc::bootstrapDatabase).useProperties("test-jdbc-connection");

        //Write continuously data into database
        jLegMed.newFlowGraph("writeToDatabase")
                .every(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)

                .and().processWith( data -> new TestData(data, "Hello World " + data))
                .and().processWith( jdbc::insertTestData).useProperties("test-jdbc-connection")
                .and().processWith(writerCollector::collect );

        //read continuously from a database
        jLegMed.newFlowGraph("readFromDatabase using PreparedStatement")
                .every(10, MILLISECONDS)
                .receive(TestData.class).from(jdbc::readTestDataPreparedStatement).useProperties("test-jdbc-connection")
                .and().processWith(readerCollector::collect );

        //Act
        jLegMed.start();

        //Assert
        await().atMost(3, SECONDS).until(
                () -> readerCollector.getNumberOfReceivedMessages() >= 10
                        && writerCollector.getNumberOfReceivedMessages() >= 10
        );

        assertEquals(writerCollector.getMessages().get(0), readerCollector.getMessages().get(0));
        assertEquals(writerCollector.getMessages().get(1), readerCollector.getMessages().get(1));
        assertEquals(writerCollector.getMessages().get(2), readerCollector.getMessages().get(2));
        assertEquals(writerCollector.getMessages().get(3), readerCollector.getMessages().get(3));
    }


    @Test
    void readFromDatabaseQueryBuilder() {
        //Arrange
        var writerCollector = new GenericCollector<TestData>();
        var readerCollector = new GenericCollector<TestData>();

        var jdbc = new JDBCStatementsForTestData();

        //First, we bootstrap database
        jLegMed.bootstrapFlowGraph("bootstrap database").execute(jdbc::bootstrapDatabase).useProperties("test-jdbc-connection");

        jLegMed.newFlowGraph("writeToDatabase")
                .every(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)

                .and().processWith(data -> new TestData(data, "Hello World " + data))
                .and().processWith( jdbc::insertTestData ).useProperties("test-jdbc-connection")
                .and().processWith(writerCollector::collect );


        jLegMed.newFlowGraph("readFromDatabase using JDBCQueryBuilder")
                .every(10, MILLISECONDS)
                .receive(TestData.class).from(jdbc::readTestDataQueryBuilder).useProperties("test-jdbc-connection")
                .and().processWith(readerCollector::collect );
        //Act
        jLegMed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> readerCollector.getNumberOfReceivedMessages() >= 10
                && writerCollector.getNumberOfReceivedMessages() >= 10);

        assertEquals(writerCollector.getMessages().get(0), readerCollector.getMessages().get(0));
        assertEquals(writerCollector.getMessages().get(1), readerCollector.getMessages().get(1));
        assertEquals(writerCollector.getMessages().get(2), readerCollector.getMessages().get(2));
        assertEquals(writerCollector.getMessages().get(3), readerCollector.getMessages().get(3));
    }

}
