package io.jexxa.jlegmed.plugins.persistence.jdbc;

import io.jexxa.common.facade.jdbc.JDBCConnection;
import io.jexxa.common.facade.jdbc.JDBCQuery;
import io.jexxa.common.facade.jdbc.builder.JDBCCommandBuilder;
import io.jexxa.common.facade.jdbc.builder.JDBCQueryBuilder;
import io.jexxa.common.facade.jdbc.builder.JDBCTableBuilder;

import java.util.List;
import java.util.Properties;

public class JDBCSession {
    private final JDBCConnection connection;

    JDBCSession(JDBCConnection connection)
    {
        this.connection = connection;
    }

    public <T extends Enum<T>> JDBCQueryBuilder<T> createQuery(Class<T> schema)
    {
        return this.connection.createQuery(schema);
    }

    public <T extends Enum<T>> JDBCCommandBuilder<T> createCommand(Class<T> schema)
    {
        return this.connection.createCommand(schema);
    }

    @SuppressWarnings("unused")
    public <T extends Enum<T>> JDBCCommandBuilder<T> createCommand()
    {
        return this.connection.createCommand();
    }

    public <T extends Enum<T>> JDBCTableBuilder<T> createTableCommand(Class<T> schema)
    {
        return this.connection.createTableCommand(schema);
    }

    public void autocreateDatabase(final Properties properties)
    {
        this.connection.autocreateDatabase(properties);
    }

    public JDBCQuery preparedStatement(String sqlStatement, List<Object> arguments)
    {
        return new JDBCQuery(connection::validateConnection, sqlStatement, arguments);
    }
}
