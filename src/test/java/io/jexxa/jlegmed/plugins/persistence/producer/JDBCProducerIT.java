package io.jexxa.jlegmed.plugins.persistence.producer;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.GenericProducer;
import io.jexxa.jlegmed.plugins.generic.processor.GenericCollector;
import io.jexxa.jlegmed.plugins.persistence.JDBCStatementsForTestData;
import io.jexxa.jlegmed.plugins.persistence.TestData;
import org.junit.jupiter.api.Test;

import static io.jexxa.jlegmed.plugins.persistence.processor.JDBCProcessor.jdbcProcessor;
import static io.jexxa.jlegmed.plugins.persistence.producer.JDBCProducer.jdbcProducer;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JDBCProducerIT {

    @Test
    void readWriteToDatabase() {
        //Arrange
        var writerCollector = new GenericCollector<TestData>();
        var readerCollectorPreparedStatement = new GenericCollector<TestData>();
        var readerCollectorQueryBuilder = new GenericCollector<TestData>();

        var jdbc = new JDBCStatementsForTestData();
        var jlegmed = new JLegMed(JDBCProducerIT.class).disableBanner();

        jlegmed.newFlowGraph("writeToDatabase")
                .each(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)

                .and().processWith( data -> new TestData(data, "Hello World " + data))
                .and().processWith( jdbcProcessor( jdbc::insertTestData )).useProperties("test-jdbc-connection")
                .and().processWith(writerCollector::collect);

        jlegmed.newFlowGraph("readFromDatabase using PreparedStatement")
                .each(10, MILLISECONDS)
                .receive(TestData.class).from(jdbcProducer(jdbc::readTestDataPreparedStatement)).useProperties("test-jdbc-connection")

                .and().processWith(readerCollectorPreparedStatement::collect);


        jlegmed.newFlowGraph("readFromDatabase using JDBCQueryBuilder")
                .each(10, MILLISECONDS)
                .receive(TestData.class).from(jdbcProducer(jdbc::readTestDataQueryBuilder)).useProperties("test-jdbc-connection")

                .and().processWith(readerCollectorQueryBuilder::collect);

        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> readerCollectorPreparedStatement.getNumberOfReceivedMessages() >= 10
                && writerCollector.getNumberOfReceivedMessages() >= 10);

        assertEquals(writerCollector.getMessages().get(0), readerCollectorPreparedStatement.getMessages().get(0));
        assertEquals(writerCollector.getMessages().get(1), readerCollectorPreparedStatement.getMessages().get(1));
        assertEquals(writerCollector.getMessages().get(2), readerCollectorPreparedStatement.getMessages().get(2));
        assertEquals(writerCollector.getMessages().get(3), readerCollectorPreparedStatement.getMessages().get(3));

        assertEquals(writerCollector.getMessages().get(0), readerCollectorQueryBuilder.getMessages().get(0));
        assertEquals(writerCollector.getMessages().get(1), readerCollectorQueryBuilder.getMessages().get(1));
        assertEquals(writerCollector.getMessages().get(2), readerCollectorQueryBuilder.getMessages().get(2));
        assertEquals(writerCollector.getMessages().get(3), readerCollectorQueryBuilder.getMessages().get(3));

        jlegmed.stop();
    }
}
