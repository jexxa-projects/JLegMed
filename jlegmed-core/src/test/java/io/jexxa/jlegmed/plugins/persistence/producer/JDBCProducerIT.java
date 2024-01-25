package io.jexxa.jlegmed.plugins.persistence.producer;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.GenericProducer;
import io.jexxa.jlegmed.plugins.generic.processor.GenericCollector;
import io.jexxa.jlegmed.plugins.persistence.JDBCStatementsForTestData;
import io.jexxa.jlegmed.plugins.persistence.TestData;
import org.junit.jupiter.api.Test;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JDBCProducerIT {

    @Test
    void readFromDatabasePreparedStatements() {
        //Arrange
        var writerCollector = new GenericCollector<TestData>();
        var readerCollector = new GenericCollector<TestData>();

        var jdbc = new JDBCStatementsForTestData();
        var jlegmed = new JLegMed(JDBCProducerIT.class).disableBanner();

        //Write to a database
        jlegmed.newFlowGraph("writeToDatabase")
                .every(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)

                .and().processWith( data -> new TestData(data, "Hello World " + data))
                .and().processWith( jdbc::insertTestData).useProperties("test-jdbc-connection")
                .and().processWith(writerCollector::collect );

        //read from a database
        jlegmed.newFlowGraph("readFromDatabase using PreparedStatement")
                .every(10, MILLISECONDS)
                .receive(TestData.class).from(jdbc::readTestDataPreparedStatement).useProperties("test-jdbc-connection")
                .and().processWith(readerCollector::collect );

        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> readerCollector.getNumberOfReceivedMessages() >= 10
                && writerCollector.getNumberOfReceivedMessages() >= 10);

        assertEquals(writerCollector.getMessages().get(0), readerCollector.getMessages().get(0));
        assertEquals(writerCollector.getMessages().get(1), readerCollector.getMessages().get(1));
        assertEquals(writerCollector.getMessages().get(2), readerCollector.getMessages().get(2));
        assertEquals(writerCollector.getMessages().get(3), readerCollector.getMessages().get(3));
        jlegmed.stop();
    }


    @Test
    void readFromDatabaseQueryBuilder() {
        //Arrange
        var writerCollector = new GenericCollector<TestData>();
        var readerCollector = new GenericCollector<TestData>();

        var jdbc = new JDBCStatementsForTestData();
        var jlegmed = new JLegMed(JDBCProducerIT.class).disableBanner();

        jlegmed.newFlowGraph("writeToDatabase")
                .every(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)

                .and().processWith( data -> new TestData(data, "Hello World " + data))
                .and().processWith( jdbc::insertTestData ).useProperties("test-jdbc-connection")
                .and().processWith(writerCollector::collect );


        jlegmed.newFlowGraph("readFromDatabase using JDBCQueryBuilder")
                .every(10, MILLISECONDS)
                .receive(TestData.class).from(jdbc::readTestDataQueryBuilder).useProperties("test-jdbc-connection")
                .and().processWith(readerCollector::collect );

        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> readerCollector.getNumberOfReceivedMessages() >= 10
                && writerCollector.getNumberOfReceivedMessages() >= 10);

        assertEquals(writerCollector.getMessages().get(0), readerCollector.getMessages().get(0));
        assertEquals(writerCollector.getMessages().get(1), readerCollector.getMessages().get(1));
        assertEquals(writerCollector.getMessages().get(2), readerCollector.getMessages().get(2));
        assertEquals(writerCollector.getMessages().get(3), readerCollector.getMessages().get(3));

        jlegmed.stop();
    }

}
