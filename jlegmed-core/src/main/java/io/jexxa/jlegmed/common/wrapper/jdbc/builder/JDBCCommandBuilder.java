package io.jexxa.jlegmed.common.wrapper.jdbc.builder;

import io.jexxa.jlegmed.common.wrapper.jdbc.JDBCCommand;
import io.jexxa.jlegmed.common.wrapper.jdbc.JDBCConnection;

import java.util.function.Supplier;


@SuppressWarnings("unused")
public class JDBCCommandBuilder<T extends Enum<T>> extends JDBCBuilder<T>
{
    private final Supplier<JDBCConnection> jdbcConnection;

    public JDBCCommandBuilder(Supplier<JDBCConnection> jdbcConnection )
    {
        this.jdbcConnection = jdbcConnection;
    }

    public JDBCCommandBuilder<T> update(T element)
    {
        getStatementBuilder()
                .append(SQLSyntax.UPDATE)
                .append(element.name())
                .append(SQLSyntax.BLANK);

        return this;
    }

    public JDBCCommandBuilder<T> update(String table)
    {
        getStatementBuilder()
                .append(SQLSyntax.UPDATE)
                .append(table)
                .append(SQLSyntax.BLANK);

        return this;
    }
    public JDBCCommandBuilder<T> update(Class<?> clazz)
    {
        getStatementBuilder()
                .append(SQLSyntax.UPDATE)
                .append(clazz.getSimpleName())
                .append(SQLSyntax.BLANK);

        return this;
    }

    public JDBCCommandBuilder<T> insertInto(T element)
    {
        getStatementBuilder()
                .append(SQLSyntax.INSERT_INTO)
                .append(element.name())
                .append(SQLSyntax.BLANK);

        return this;
    }

    public JDBCCommandBuilder<T> insertInto(String table)
    {
        getStatementBuilder()
                .append(SQLSyntax.INSERT_INTO)
                .append(table)
                .append(SQLSyntax.BLANK);

        return this;
    }

    public JDBCCommandBuilder<T> insertInto(Class<?> clazz)
    {
        getStatementBuilder()
                .append(SQLSyntax.INSERT_INTO)
                .append(clazz.getSimpleName())
                .append(SQLSyntax.BLANK);

        return this;
    }

    public JDBCCommandBuilder<T> values(Object[] args)
    {
        getStatementBuilder().append("values ( ");
        getStatementBuilder().append( SQLSyntax.ARGUMENT_PLACEHOLDER ); // Handle first entry (without COMMA)
        addArgument(args[0]);

        for(var i = 1;  i < args.length; ++i ) // Handle remaining entries(with leading COMMA)
        {
            getStatementBuilder().append( SQLSyntax.COMMA );
            getStatementBuilder().append( SQLSyntax.ARGUMENT_PLACEHOLDER );
            addArgument(args[i]);
        }
        getStatementBuilder().append(")");

        return this;
    }

    public JDBCCommandBuilder<T> values(JDBCObject[] args)
    {
        getStatementBuilder().append("values ( ");
        getStatementBuilder().append( args[0].getBindParameter()); // Handle first entry (without COMMA)
        addArgument(args[0].getJdbcValue());

        for(var i = 1;  i < args.length; ++i ) // Handle remaining entries(with leading COMMA)
        {
            getStatementBuilder().append( SQLSyntax.COMMA );
            getStatementBuilder().append( args[i].getBindParameter() );
            addArgument(args[i].getJdbcValue());
        }
        getStatementBuilder().append(")");

        return this;
    }

    public JDBCCommandBuilder<T> columns(String... args)
    {
        getStatementBuilder().append("( ");
        getStatementBuilder().append(args[0]);

        for(var i = 1;  i < args.length; ++i ) // Handle remaining entries(with leading COMMA)
        {
            getStatementBuilder().append( SQLSyntax.COMMA );
            getStatementBuilder().append( args[i] );
        }
        getStatementBuilder().append(" ) ");

        return this;
    }

    //CREATE UNIQUE INDEX JexxaInboundMessage_repository_key ON JexxaInboundMessage (repository_key)
    public JDBCCommandBuilder<T> createUniqueIndex(String indexName)
    {
        getStatementBuilder()
                .append("CREATE UNIQUE INDEX ")
                .append(indexName);
        return this;
    }

    public JDBCCommandBuilder<T> createIndex(String indexName)
    {
        getStatementBuilder()
                .append("CREATE INDEX ")
                .append(indexName);
        return this;
    }

    public JDBCCommandBuilder<T> on(String table, String... columns)
    {
        getStatementBuilder()
                .append(" ON ")
                .append(table)
                .append("(")
                .append(columns[0]);

        for(var i = 1;  i < columns.length; ++i ) // Handle remaining entries(with leading COMMA)
        {
            getStatementBuilder().append( SQLSyntax.COMMA );
            getStatementBuilder().append( columns[i] );
        }
        getStatementBuilder().append(" ) ");

        return this;
    }




    public JDBCCommandBuilder<T> deleteFrom(T element)
    {
        getStatementBuilder()
                .append(SQLSyntax.DELETE)
                .append(SQLSyntax.FROM)
                .append(element.name())
                .append(SQLSyntax.BLANK);

        return this;
    }

    public JDBCCommandBuilder<T> deleteFrom(Class<?> clazz)
    {
        getStatementBuilder()
                .append(SQLSyntax.DELETE)
                .append(SQLSyntax.FROM)
                .append(clazz.getSimpleName())
                .append(SQLSyntax.BLANK);

        return this;
    }

    public JDBCCondition<T, JDBCCommandBuilder<T>> where(T element)
    {
        getStatementBuilder()
                .append(SQLSyntax.WHERE)
                .append(element.name())
                .append(SQLSyntax.BLANK);

        return new JDBCCondition<>(this);
    }

    public JDBCCondition<T, JDBCCommandBuilder<T>> where(String element)
    {
        getStatementBuilder()
                .append(SQLSyntax.WHERE)
                .append(element)
                .append(SQLSyntax.BLANK);

        return new JDBCCondition<>(this);
    }

    public JDBCCondition<T, JDBCCommandBuilder<T>> and(T element)
    {
        getStatementBuilder()
                .append(SQLSyntax.AND)
                .append(element.name())
                .append(SQLSyntax.BLANK);

        return new JDBCCondition<>(this);
    }

    public JDBCCondition<T, JDBCCommandBuilder<T>> or(T element)
    {
        getStatementBuilder()
                .append(SQLSyntax.OR)
                .append(element.name())
                .append(SQLSyntax.BLANK);

        return new JDBCCondition<>(this);
    }

    public JDBCCommandBuilder<T> set(T element, JDBCObject value)
    {
        getStatementBuilder()
                .append(SQLSyntax.SET)
                .append(element.name())
                .append(SQLSyntax.SQLOperation.EQUAL)
                .append(value.getBindParameter());

        addArgument(value.getJdbcValue());
        return this;
    }

    public JDBCCommandBuilder<T> set(String[] element, JDBCObject[] value)
    {
        getStatementBuilder()
                .append(SQLSyntax.SET);

        for (var i = 0; i < element.length; ++i)
        {
            addArgument(value[i].getJdbcValue());
            getStatementBuilder()
                    .append( element[i] )
                    .append(SQLSyntax.SQLOperation.EQUAL)
                    .append(value[i].getBindParameter());
            if ( i < element.length - 1)
            {
                getStatementBuilder().append(SQLSyntax.COMMA);
            }
        }
        return this;
    }


    public JDBCCommand create()
    {
        return new JDBCCommand(jdbcConnection, getStatementBuilder().toString(), getArguments() );
    }
}
