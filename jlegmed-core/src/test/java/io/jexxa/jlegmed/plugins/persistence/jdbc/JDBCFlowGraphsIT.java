package io.jexxa.jlegmed.plugins.persistence.jdbc;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.GenericProducer;
import io.jexxa.jlegmed.plugins.generic.processor.GenericCollector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JDBCFlowGraphsIT {
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
    void writeToDatabase() {
        //Arrange
        JDBCSessionPool.init();

        var messageCollector = new GenericCollector<DataToBeStored>();
        var database = new JDBCStatements();

        jLegMed.bootstrapFlowGraph("bootstrap database").execute(database::bootstrapDatabase).useProperties("test-jdbc-connection");

        jLegMed.newFlowGraph("HelloWorld")

                .every(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)

                .and().processWith( data -> new DataToBeStored(data, "Hello World " + data))
                .and().processWith( database::insert).useProperties("test-jdbc-connection")
                .and().consumeWith( messageCollector::collect );
        //Act
        jLegMed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 10);
    }

    @Test
    void readFromDatabase() {
        //Arrange
        JDBCSessionPool.init();

        var expectedData = new GenericCollector<DataToBeStored>();
        var result = new GenericCollector<DataToBeStored>();

        var objectUnderTest = new JDBCStatements();

        // reset the database as part of bootstrapping
        jLegMed.bootstrapFlowGraph("reset database")
                .execute(objectUnderTest::bootstrapDatabase).useProperties("test-jdbc-connection");

        jLegMed.bootstrapFlowGraph("init test data")
                .repeat(10)
                .receive(Integer.class).from(GenericProducer::counter)

                .and().processWith( data -> new DataToBeStored(data, "Hello World " + data) )
                .and().processWith( objectUnderTest::insert ).useProperties("test-jdbc-connection")
                .and().consumeWith( expectedData::collect );

        //Act
        jLegMed.newFlowGraph("readFromDatabase using PreparedStatement")
                .every(10, MILLISECONDS)
                .receive(DataToBeStored.class).from(objectUnderTest::readWithPreparedStatement).useProperties("test-jdbc-connection")

                .and().consumeWith( result::collect );

        jLegMed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> expectedData.getNumberOfReceivedMessages() >= 10 );

        assertEquals(expectedData.getMessages().get(0), result.getMessages().get(0));
        assertEquals(expectedData.getMessages().get(1), result.getMessages().get(1));
        assertEquals(expectedData.getMessages().get(2), result.getMessages().get(2));
        assertEquals(expectedData.getMessages().get(3), result.getMessages().get(3));
    }


    @Test
    void readWritePreparedStatements() {
        //Arrange
        JDBCSessionPool.init();

        var writerCollector = new GenericCollector<DataToBeStored>();
        var readerCollector = new GenericCollector<DataToBeStored>();

        var jdbc = new JDBCStatements();

        // reset the database as part of bootstrapping
        jLegMed.bootstrapFlowGraph("reset database")
                .execute(jdbc::bootstrapDatabase).useProperties("test-jdbc-connection");

        //Write continuously data into database
        jLegMed.newFlowGraph("writeToDatabase")
                .every(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)

                .and().processWith( data -> new DataToBeStored(data, "Hello World " + data) )
                .and().processWith( jdbc::insert ).useProperties("test-jdbc-connection")
                .and().consumeWith( writerCollector::collect );

        //read continuously from a database
        jLegMed.newFlowGraph("readFromDatabase using PreparedStatement")
                .every(10, MILLISECONDS)
                .receive(DataToBeStored.class).from(jdbc::readWithPreparedStatement).useProperties("test-jdbc-connection")

                .and().consumeWith( readerCollector::collect );

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
    void readWriteQueryBuilder() {
        //Arrange
        var writerCollector = new GenericCollector<DataToBeStored>();
        var readerCollector = new GenericCollector<DataToBeStored>();

        var jdbc = new JDBCStatements();

        //First, we bootstrap database
        jLegMed.bootstrapFlowGraph("bootstrap database").execute(jdbc::bootstrapDatabase).useProperties("test-jdbc-connection");

        jLegMed.newFlowGraph("writeToDatabase")
                .every(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)

                .and().processWith(data -> new DataToBeStored(data, "Hello World " + data))
                .and().processWith( jdbc::insert).useProperties("test-jdbc-connection")
                .and().processWith(writerCollector::collect );


        jLegMed.newFlowGraph("readFromDatabase using JDBCQueryBuilder")
                .every(10, MILLISECONDS)
                .receive(DataToBeStored.class).from(jdbc::readWithQueryBuilder).useProperties("test-jdbc-connection")
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
