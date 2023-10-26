package io.jexxa.jlegmed.plugins.persistence;

import io.jexxa.jlegmed.common.wrapper.jdbc.JDBCConnection;
import io.jexxa.jlegmed.common.wrapper.jdbc.JDBCConnectionPool;
import io.jexxa.jlegmed.common.wrapper.jdbc.builder.JDBCTableBuilder;
import io.jexxa.jlegmed.common.wrapper.jdbc.builder.SQLDataType;
import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.core.filter.Context;
import io.jexxa.jlegmed.plugins.generic.GenericProducer;
import io.jexxa.jlegmed.plugins.generic.MessageCollector;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import io.jexxa.jlegmed.plugins.persistence.processor.SQLWriter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class DatabaseReaderIT {

    @BeforeAll
    static void beforeAll()
    {
        JLegMed jLegMed = new JLegMed(DatabaseReaderIT.class);
        var context = new Context(jLegMed.getProperties());
        var dbProperties = context.getProperties("test-jdbc-connection").orElseThrow();
        createDatabase( dbProperties, jLegMed);
        createTable("DATABASE_READER_IT", dbProperties, jLegMed);
    }

    @Test
    void writeToDatabase() {
        //Arrange
        var messageCollector = new MessageCollector<TestData>();

        var jlegmed = new JLegMed(DatabaseReaderIT.class);

        jlegmed.newFlowGraph("HelloWorld")

                .each(500, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)

                .andProcessWith( data -> new TestData(data, "Hello World " + data))
                .andProcessWith(GenericProcessors::consoleLogger)
                .andProcessWith( testJDBCWriter()).useProperties("test-jdbc-connection")
                .andProcessWith(messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
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
                .addColumn(DBSchema.DB_INDEX, SQLDataType.INTEGER).addConstraint(JDBCTableBuilder.SQLConstraint.PRIMARY_KEY)
                .addColumn(DBSchema.DB_STRING_DATA, SQLDataType.VARCHAR)
                .create()
                .asIgnore();

    }

    static  SQLWriter<TestData> testJDBCWriter()
    {
        return new SQLWriter<>() {
            @Override
            protected void executeCommand(JDBCConnection jdbcConnection, TestData element) {
                jdbcConnection.createCommand(DBSchema.class).
                        insertInto("DATABASE_READER_IT").values(new Object[]{element.index, element.message})
                        .create()
                        .asUpdate();
            }
        };
    }


    private enum DBSchema {
        DB_INDEX,
        DB_STRING_DATA;
    }
}
