package io.jexxa.jlegmed.plugins.persistence.processor;

import io.jexxa.jlegmed.common.wrapper.jdbc.JDBCConnection;
import io.jexxa.jlegmed.common.wrapper.jdbc.JDBCConnectionPool;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.filter.processor.Processor;

import java.util.Properties;
import java.util.function.Function;

public abstract class SQLWriter<T> extends Processor<T, T> {

    private Properties properties;
    @Override
    public void init()
    {
        super.init();

        this.properties = getProperties()
                .orElseThrow( () -> new IllegalArgumentException("No properties for database connection defined -> Define properties of SQLWriter in your main"))
                .properties();
    }

    protected T doProcess(T content, FilterContext context)
    {
        var jdbcConnection = JDBCConnectionPool.getConnection(properties, this);


        executeCommand(jdbcConnection, content);

        return content;
    }

    protected abstract void executeCommand(JDBCConnection jdbcConnection, T element);



    public static <T> SQLWriter<T> insert(String table, Function<T, Object[]> objectMapper)
    {
        return new SQLWriter<>() {
            @Override
            protected void executeCommand(JDBCConnection jdbcConnection, T element) {
                jdbcConnection.createCommand().
                        insertInto(table).values(objectMapper.apply(element))
                        .create()
                        .asUpdate();
            }
        };
    }

    public static <T> SQLWriter<T> createTable(String table, Function<T, Object[]> objectMapper) {
        return new SQLWriter<>() {
            private boolean initialized = false;
            @Override
            protected void executeCommand(JDBCConnection connection, T element) {

                if (!initialized) {
                    connection.autocreateDatabase(getState().getPropertiesConfig().properties());

               /*     connection.createTableCommand(DBSchema.class)
                            .createTableIfNotExists(tableName)
                            .addColumn(DB_INDEX, SQLDataType.INTEGER).addConstraint(JDBCTableBuilder.SQLConstraint.PRIMARY_KEY)
                            .addColumn(DBSchema.DB_STRING_DATA, SQLDataType.VARCHAR)
                            .create()
                            .asIgnore();
*/
                    this.initialized = true;
                }
            }
        };
    }
}
