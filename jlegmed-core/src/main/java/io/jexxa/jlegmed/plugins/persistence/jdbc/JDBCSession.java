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

    public <T extends Enum<T>> JDBCQueryBuilder<T> query(Class<T> schema)
    {
        return this.connection.query(schema);
    }

    public <T extends Enum<T>> JDBCCommandBuilder<T> command(Class<T> schema)
    {
        return this.connection.command(schema);
    }

    @SuppressWarnings("unused")
    public <T extends Enum<T>> JDBCCommandBuilder<T> command()
    {
        return this.connection.command();
    }

    public <T extends Enum<T>> JDBCTableBuilder<T> tableCommand(Class<T> schema)
    {
        return this.connection.tableCommand(schema);
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
