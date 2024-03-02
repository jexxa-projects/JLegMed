package io.jexxa.jlegmed.plugins.persistence.jdbc;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.GenericProducer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JDBCFlowGraphsIT {
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
    void writeToDatabase() {
        //Arrange
        var messageCollector = new Stack<DataToBeStored>();
        var database = new JDBCStatements();

        jLegMed.bootstrapFlowGraph("bootstrap database")
                .execute(database::bootstrapDatabase).useProperties("test-jdbc-connection");

        jLegMed.newFlowGraph("HelloWorld")

                .every(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)

                .and().processWith( data -> new DataToBeStored(data, "Hello World " + data))
                .and().processWith( database::insert).useProperties("test-jdbc-connection")
                .and().consumeWith( messageCollector::push );
        //Act
        jLegMed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.size() >= 10);
    }

    @Test
    void readFromDatabase() {
        //Arrange
        var expectedData = new Stack<DataToBeStored>();
        var result = new Stack<DataToBeStored>();

        var objectUnderTest = new JDBCStatements();

        // Bootstrap database and database schema
        jLegMed.bootstrapFlowGraph("bootstrap database")
                .execute(objectUnderTest::bootstrapDatabase).useProperties("test-jdbc-connection");

        // Init some test data that should be read
        jLegMed.bootstrapFlowGraph("init test data")
                .repeat(10)
                .receive(Integer.class).from(GenericProducer::counter)

                .and().processWith( data -> new DataToBeStored(data, "Hello World " + data) )
                .and().processWith( objectUnderTest::insert ).useProperties("test-jdbc-connection")
                .and().consumeWith( expectedData::push );

        // Read all data from the database with a delay of 10ms between reads
        jLegMed.newFlowGraph("readFromDatabase using PreparedStatement")
                .every(10, MILLISECONDS)
                .receive(DataToBeStored.class).from(objectUnderTest::readWithPreparedStatement).useProperties("test-jdbc-connection")

                .and().consumeWith( result::push );

        //Act
        jLegMed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> expectedData.size() >= 10 );

        assertEquals(expectedData.toArray()[0], result.toArray()[0]);
        assertEquals(expectedData.toArray()[1], result.toArray()[1]);
        assertEquals(expectedData.toArray()[2], result.toArray()[2]);
        assertEquals(expectedData.toArray()[3], result.toArray()[3]);
    }


    @Test
    void parallelReadWritePreparedStatements() {
        //Arrange
        var writerCollector = new Stack<DataToBeStored>();
        var readerCollector = new Stack<DataToBeStored>();

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
                .and().consumeWith( writerCollector::push );

        //read continuously from a database
        jLegMed.newFlowGraph("readFromDatabase using PreparedStatement")
                .every(10, MILLISECONDS)
                .receive(DataToBeStored.class).from(jdbc::readWithPreparedStatement).useProperties("test-jdbc-connection")

                .and().consumeWith( readerCollector::push );

        //Act
        jLegMed.start();

        //Assert
        await().atMost(3, SECONDS).until(
                () -> readerCollector.size() >= 10
                        && writerCollector.size() >= 10
        );

        assertEquals(writerCollector.toArray()[0], readerCollector.toArray()[0]);
        assertEquals(writerCollector.toArray()[1], readerCollector.toArray()[1]);
        assertEquals(writerCollector.toArray()[2], readerCollector.toArray()[2]);
        assertEquals(writerCollector.toArray()[3], readerCollector.toArray()[3]);
    }


    @Test
    void parallelReadWriteQueryBuilder() {
        //Arrange
        var writerCollector = new Stack<DataToBeStored>();
        var readerCollector = new Stack<DataToBeStored>();

        var jdbc = new JDBCStatements();

        //First, we bootstrap database
        jLegMed.bootstrapFlowGraph("bootstrap database").execute(jdbc::bootstrapDatabase).useProperties("test-jdbc-connection");

        //Write continuously data into database
        jLegMed.newFlowGraph("writeToDatabase")
                .every(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)

                .and().processWith(data -> new DataToBeStored(data, "Hello World " + data))
                .and().processWith( jdbc::insert).useProperties("test-jdbc-connection")
                .and().processWith(writerCollector::push );

        //read continuously from a database
        jLegMed.newFlowGraph("readFromDatabase using JDBCQueryBuilder")
                .every(10, MILLISECONDS)
                .receive(DataToBeStored.class).from(jdbc::readWithQueryBuilder).useProperties("test-jdbc-connection")
                .and().processWith(readerCollector::push );
        //Act
        jLegMed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> readerCollector.size() >= 10
                && writerCollector.size() >= 10);

        assertEquals(writerCollector.toArray()[0], readerCollector.toArray()[0]);
        assertEquals(writerCollector.toArray()[1], readerCollector.toArray()[1]);
        assertEquals(writerCollector.toArray()[2], readerCollector.toArray()[2]);
        assertEquals(writerCollector.toArray()[3], readerCollector.toArray()[3]);
    }

}
