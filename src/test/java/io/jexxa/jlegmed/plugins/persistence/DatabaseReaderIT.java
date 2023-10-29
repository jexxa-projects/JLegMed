package io.jexxa.jlegmed.plugins.persistence;

import io.jexxa.jlegmed.common.wrapper.jdbc.JDBCConnection;
import io.jexxa.jlegmed.common.wrapper.jdbc.JDBCQuery;
import io.jexxa.jlegmed.common.wrapper.jdbc.builder.JDBCTableBuilder;
import io.jexxa.jlegmed.common.wrapper.jdbc.builder.SQLDataType;
import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.GenericProducer;
import io.jexxa.jlegmed.plugins.generic.MessageCollector;
import io.jexxa.jlegmed.plugins.persistence.processor.JDBCContext;
import io.jexxa.jlegmed.plugins.persistence.processor.SQLWriter;
import io.jexxa.jlegmed.plugins.persistence.producer.SQLReader;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static io.jexxa.jlegmed.plugins.persistence.DatabaseReaderIT.DatabaseMethods.DBSchema.DB_INDEX;
import static io.jexxa.jlegmed.plugins.persistence.DatabaseReaderIT.DatabaseMethods.DBSchema.DB_STRING_DATA;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.commons.lang3.ArrayUtils.toArray;
import static org.awaitility.Awaitility.await;

class DatabaseReaderIT {

    @Test
    void writeToDatabase() {
        //Arrange
        var messageCollector = new MessageCollector<TestData>();
        var database = new DatabaseMethods();

        var jlegmed = new JLegMed(DatabaseReaderIT.class).disableBanner();

        jlegmed.newFlowGraph("HelloWorld")

                .each(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)

                .and().processWith( data -> new TestData(data, "Hello World " + data))

                .and().processWith( SQLWriter.execute(database::initDatabase)).useProperties("test-jdbc-connection")
                .and().processWith( SQLWriter.execute(database::insertData )).useProperties("test-jdbc-connection")

                .and().processWith(messageCollector::collect);
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
        var database = new DatabaseMethods();

        var jlegmed = new JLegMed(DatabaseReaderIT.class).disableBanner();

        jlegmed.newFlowGraph("HelloWorld")

                .each(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)

                .and().processWith( data -> new TestData(data, "Hello World " + data))
                .and().processWith( SQLWriter.execute( database::initDatabase )).useProperties("test-jdbc-connection")
                .and().processWith( SQLWriter.execute( database::insertData )).useProperties("test-jdbc-connection")
                .and().processWith(messageCollector::collect);
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

        var jlegmed = new JLegMed(DatabaseReaderIT.class).disableBanner();
        var database = new DatabaseMethods();

        jlegmed.newFlowGraph("writeToDatabase")
                .each(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)

                .and().processWith( data -> new TestData(data, "Hello World " + data))
                .and().processWith( SQLWriter.execute( database::initDatabase )).useProperties("test-jdbc-connection")
                .and().processWith( SQLWriter.execute( database::insertData )).useProperties("test-jdbc-connection");

        jlegmed.newFlowGraph("readFromDatabase")
                .each(10, MILLISECONDS)
                .receive(TestData.class).from(testDataSQLReader()).useProperties("test-jdbc-connection")

                .and().processWith(messageCollector::collect);

        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 10);
        jlegmed.stop();
    }

    public record TestData(int index, String message){}


    static SQLReader<TestData> testDataSQLReader()
    {
        return new SQLReader<>() {
            @Override
            protected JDBCQuery getQuery(JDBCConnection jdbcConnection) {
                var currentMax = jdbcConnection
                        .createQuery(DatabaseMethods.DBSchema.class)
                        .selectMax(DB_INDEX).from(DatabaseMethods.DBSchema.DATABASE_READER_IT).create()
                        .asInt().findFirst()
                        .orElse(0);

                var stateID = "sqlIndex";
                var lastPublishedIndex = filterContext().state(stateID, Integer.class).orElse(0);
                filterContext().updateState(stateID, currentMax);

                return jdbcConnection
                        .createQuery(DatabaseMethods.DBSchema.class)
                        .selectAll().from(DatabaseMethods.DBSchema.DATABASE_READER_IT)
                        .where(DB_INDEX).isGreaterThan(lastPublishedIndex).and(DB_INDEX).isLessOrEqual(currentMax)
                        .create();
            }

            @Override
            protected TestData readAs(ResultSet resultSet) throws SQLException {
                return new TestData(resultSet.getInt(DB_INDEX.name()), resultSet.getString(DB_STRING_DATA.name()));
            }
        };
    }

    public static class DatabaseMethods {
        private boolean databaseInitialized = false;
        enum DBSchema {
            DB_INDEX,
            DB_STRING_DATA,

            DATABASE_READER_IT
        }

        synchronized public void initDatabase(JDBCContext jdbcContext) {
            if (!databaseInitialized) {
                createDatabase(jdbcContext);
                createTable(jdbcContext);
                databaseInitialized = true;
            }
        }
        synchronized private void createTable(JDBCContext jdbcContext)
        {
            if (!databaseInitialized) {
                jdbcContext.jdbcConnection().createTableCommand(DatabaseMethods.DBSchema.class)
                        .dropTableIfExists(DBSchema.DATABASE_READER_IT)
                        .asIgnore();

                jdbcContext.jdbcConnection().createTableCommand(DBSchema.class)
                        .createTableIfNotExists(DBSchema.DATABASE_READER_IT)
                        .addColumn(DBSchema.DB_INDEX, SQLDataType.INTEGER).addConstraint(JDBCTableBuilder.SQLConstraint.PRIMARY_KEY)
                        .addColumn(DBSchema.DB_STRING_DATA, SQLDataType.VARCHAR)
                        .create()
                        .asIgnore();
                databaseInitialized = true;
            }
        }

        synchronized private void createDatabase(JDBCContext jdbcContext)
        {
            if (!databaseInitialized) {
                jdbcContext.jdbcConnection().autocreateDatabase(jdbcContext.filterContext().filterProperties().orElseThrow().properties());
            }
        }

        synchronized public void insertData(JDBCContext jdbcContext, TestData data)
        {
            jdbcContext.jdbcConnection().createCommand(DatabaseMethods.DBSchema.class).
                    insertInto(DBSchema.DATABASE_READER_IT).values(toArray(data.index, data.message))
                    .create()
                    .asUpdate();
        }
    }
}
