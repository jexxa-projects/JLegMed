package io.jexxa.jlegmed.plugins.persistence;

import io.jexxa.jlegmed.common.wrapper.jdbc.JDBCConnection;
import io.jexxa.jlegmed.common.wrapper.jdbc.JDBCConnectionPool;
import io.jexxa.jlegmed.common.wrapper.jdbc.JDBCQuery;
import io.jexxa.jlegmed.common.wrapper.jdbc.builder.JDBCTableBuilder;
import io.jexxa.jlegmed.common.wrapper.jdbc.builder.SQLDataType;
import io.jexxa.jlegmed.common.wrapper.utils.properties.PropertiesUtils;
import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.GenericProducer;
import io.jexxa.jlegmed.plugins.generic.MessageCollector;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import io.jexxa.jlegmed.plugins.persistence.processor.SQLWriter;
import io.jexxa.jlegmed.plugins.persistence.producer.SQLReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import static io.jexxa.jlegmed.plugins.persistence.DatabaseReaderIT.DBSchema.DATABASE_READER_IT;
import static io.jexxa.jlegmed.plugins.persistence.DatabaseReaderIT.DBSchema.DB_INDEX;
import static io.jexxa.jlegmed.plugins.persistence.DatabaseReaderIT.DBSchema.DB_STRING_DATA;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.commons.lang3.ArrayUtils.toArray;
import static org.awaitility.Awaitility.await;

class DatabaseReaderIT {

    @BeforeEach
    void beforeEach()
    {
        JLegMed jLegMed = new JLegMed(DatabaseReaderIT.class);
        var dbProperties = PropertiesUtils.getSubset(jLegMed.getProperties(), "test-jdbc-connection");
        createDatabase( dbProperties, jLegMed);
        createTable("DATABASE_READER_IT", dbProperties, jLegMed);
    }

    @Test
    void writeToDatabase() {
        System.out.println();
        //Arrange
        var messageCollector = new MessageCollector<TestData>();

        var jlegmed = new JLegMed(DatabaseReaderIT.class);

        jlegmed.newFlowGraph("HelloWorld")

                .each(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)

                .andProcessWith( data -> new TestData(data, "Hello World " + data))
                .andProcessWith(GenericProcessors::consoleLogger)
                .andProcessWith( SQLWriter.insert( "DATABASE_READER_IT", data -> toArray(data.index, data.message)) ).useProperties("test-jdbc-connection")
                .andProcessWith(messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 10);

        jlegmed.stop();
    }

    @Test
    void asInsert() {
        //Arrange
        var messageCollector = new MessageCollector<TestData>();

        var jlegmed = new JLegMed(DatabaseReaderIT.class);

        jlegmed.newFlowGraph("HelloWorld")

                .each(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)

                .andProcessWith( data -> new TestData(data, "Hello World " + data))
                .andProcessWith(GenericProcessors::consoleLogger)
                .andProcessWith( SQLWriter.insert( "DATABASE_READER_IT", data -> toArray(data.index, data.message)) ).useProperties("test-jdbc-connection")
                .andProcessWith(messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 10);

        jlegmed.stop();
    }

    @Test
    void readWriteToDatabase() {
        //Arrange
        var messageCollector = new MessageCollector<TestData>();

        var jlegmed = new JLegMed(DatabaseReaderIT.class);

        jlegmed.newFlowGraph("writeToDatabase")

                .each(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)

                .andProcessWith( data -> new TestData(data, "Hello World " + data))
                .andProcessWith( SQLWriter.insert( "DATABASE_READER_IT", data -> toArray(data.index, data.message)) ).useProperties("test-jdbc-connection");

        jlegmed.newFlowGraph("readFromDatabase")

                .each(10, MILLISECONDS)
                .receive(TestData.class).from(testDataSQLReader()).useProperties("test-jdbc-connection")
                .andProcessWith(GenericProcessors::consoleLogger)
                .andProcessWith(messageCollector::collect);

        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 10);
        jlegmed.stop();
    }

    private record TestData(int index, String message){}

    static void createDatabase(Properties properties, JLegMed jLegMed)
    {
        var connection = JDBCConnectionPool.getConnection(properties, jLegMed);
        connection.autocreateDatabase(properties);
    }



    static void createTable(String tableName, Properties properties, JLegMed jLegMed)
    {
        var connection = JDBCConnectionPool.getConnection(properties, jLegMed);
        connection.createTableCommand(DBSchema.class)
                .dropTableIfExists(tableName)
                .asIgnore();

        connection.createTableCommand(DBSchema.class)
                .createTableIfNotExists(tableName )
                .addColumn(DB_INDEX, SQLDataType.INTEGER).addConstraint(JDBCTableBuilder.SQLConstraint.PRIMARY_KEY)
                .addColumn(DBSchema.DB_STRING_DATA, SQLDataType.VARCHAR)
                .create()
                .asIgnore();

    }


    static SQLReader<TestData> testDataSQLReader()
    {
        return new SQLReader<>() {
            @Override
            protected JDBCQuery getQuery(JDBCConnection jdbcConnection) {
                var currentMax = jdbcConnection
                        .createQuery(DBSchema.class)
                        .selectMax(DB_INDEX).from(DATABASE_READER_IT).create()
                        .asInt().findFirst()
                        .orElse(0);

                var stateID = "sqlIndex";
                var lastPublishedIndex = filterContext().state(stateID, Integer.class).orElse(0);
                filterContext().updateState(stateID, currentMax);

                return jdbcConnection
                        .createQuery(DBSchema.class)
                        .selectAll().from(DBSchema.DATABASE_READER_IT)
                        .where(DB_INDEX).isGreaterThan(lastPublishedIndex).and(DB_INDEX).isLessOrEqual(currentMax)
                        .create();
            }

            @Override
            protected TestData readAs(ResultSet resultSet) throws SQLException {
                return new TestData(resultSet.getInt(DB_INDEX.name()), resultSet.getString(DB_STRING_DATA.name()));
            }
        };
    }

    enum DBSchema {
        DB_INDEX,
        DB_STRING_DATA,

        DATABASE_READER_IT
    }
}
