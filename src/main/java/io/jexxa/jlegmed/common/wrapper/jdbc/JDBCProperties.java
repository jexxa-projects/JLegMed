package io.jexxa.jlegmed.common.wrapper.jdbc;

public final class JDBCProperties
{
    public static final String JDBC_FILE_USERNAME = "jdbc.file.username";
    public static final String JDBC_FILE_PASSWORD = "jdbc.file.password";

    public static final String JDBC_URL = "jdbc.url";
    public static final String JDBC_USERNAME = "jdbc.username";
    public static final String JDBC_PASSWORD = "jdbc.password";
    public static final String JDBC_DRIVER = "jdbc.driver";
    public static final String JDBC_AUTOCREATE_DATABASE = "jdbc.autocreate.database";
    public static final String JDBC_AUTOCREATE_TABLE = "jdbc.autocreate.table";

    /** Defines the jdbc transaction level. This must be one of the following values "read-uncommitted", "read-committed", "repeatable-read", "serializable"*/
    public static final String JDBC_TRANSACTION_ISOLATION_LEVEL = "jdbc.transaction.isolation.level";

    public static final String REPOSITORY_STRATEGY = "repository.strategy";

    public static final String OBJECTSTORE_STRATEGY = "objectstore.strategy";

    private JDBCProperties()
    {
        //private constructor
    }

}

