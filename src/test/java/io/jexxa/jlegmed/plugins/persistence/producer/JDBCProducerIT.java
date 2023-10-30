package io.jexxa.jlegmed.plugins.persistence.producer;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.GenericProducer;
import io.jexxa.jlegmed.plugins.generic.MessageCollector;
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
        var messageCollectorWriter = new MessageCollector<TestData>();

        var messageCollectorReader = new MessageCollector<TestData>();

        var jlegmed = new JLegMed(JDBCProducerIT.class).disableBanner();
        var jdbc = new JDBCStatementsForTestData();

        jlegmed.newFlowGraph("writeToDatabase")
                .each(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)

                .and().processWith( data -> new TestData(data, "Hello World " + data))
                .and().processWith( jdbcProcessor( jdbc::insertTestData )).useProperties("test-jdbc-connection")
                .and().processWith(messageCollectorWriter::collect);

        jlegmed.newFlowGraph("readFromDatabase")
                .each(10, MILLISECONDS)
                .receive(TestData.class).from(jdbcProducer(jdbc::readTestData)).useProperties("test-jdbc-connection")

                .and().processWith(messageCollectorReader::collect);

        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollectorReader.getNumberOfReceivedMessages() >= 10
                && messageCollectorWriter.getNumberOfReceivedMessages() >= 10);

        assertEquals(messageCollectorWriter.getMessages().get(0), messageCollectorReader.getMessages().get(0));
        assertEquals(messageCollectorWriter.getMessages().get(1), messageCollectorReader.getMessages().get(1));
        assertEquals(messageCollectorWriter.getMessages().get(2), messageCollectorReader.getMessages().get(2));
        assertEquals(messageCollectorWriter.getMessages().get(3), messageCollectorReader.getMessages().get(3));

        jlegmed.stop();
    }


}
