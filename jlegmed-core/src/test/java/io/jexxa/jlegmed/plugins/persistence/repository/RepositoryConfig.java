package io.jexxa.jlegmed.plugins.persistence.repository;



import java.util.Properties;
import java.util.stream.Stream;

import static io.jexxa.common.facade.jdbc.JDBCProperties.jdbcAutocreateDatabase;
import static io.jexxa.common.facade.jdbc.JDBCProperties.jdbcAutocreateTable;
import static io.jexxa.common.facade.jdbc.JDBCProperties.jdbcDriver;
import static io.jexxa.common.facade.jdbc.JDBCProperties.jdbcPassword;
import static io.jexxa.common.facade.jdbc.JDBCProperties.jdbcTransactionIsolationLevel;
import static io.jexxa.common.facade.jdbc.JDBCProperties.jdbcUrl;
import static io.jexxa.common.facade.jdbc.JDBCProperties.jdbcUsername;
import static io.jexxa.common.facade.s3.S3Properties.s3AccessKey;
import static io.jexxa.common.facade.s3.S3Properties.s3Bucket;
import static io.jexxa.common.facade.s3.S3Properties.s3Endpoint;
import static io.jexxa.common.facade.s3.S3Properties.s3SecretKey;

public class RepositoryConfig {
    public static final String REPOSITORY_CONFIG = "io.jexxa.jlegmed.plugins.persistence.repository.RepositoryConfig#repositoryConfig";

    private static final String USER_NAME = "postgres";
    private static final String USER_PASSWORD = "admin";
    @SuppressWarnings("unused")
    public static Stream<Properties> objectStoreConfig(String schemaName) {
        return Stream.of(
                postgresRepositoryConfigSerializable(schemaName),
                postgresRepositoryConfigRepeatableRead(schemaName),
                postgresRepositoryConfig(schemaName),
                h2RepositoryConfig(),
                imdbRepositoryConfig(),
                s3RepositoryConfig()
        );
    }

    @SuppressWarnings("unused") // Used in ParameterizedTest
    public static Stream<Properties> jdbcRepositoryConfig(String schemaName) {
        return Stream.of(
                postgresRepositoryConfig(schemaName),
                h2RepositoryConfig()
        );
    }

    @SuppressWarnings("unused") // Used in ParameterizedTest
    public static Stream<Properties> repositoryConfig() {
        return Stream.of(
                postgresRepositoryConfig("dev"),
                h2RepositoryConfig(),
                imdbRepositoryConfig(),
                s3RepositoryConfig()
        );
    }

    public static Properties postgresRepositoryConfig(String schemaName) {
        var postgresProperties = new Properties();
        postgresProperties.put(jdbcDriver(), "org.postgresql.Driver");
        postgresProperties.put(jdbcPassword(), USER_PASSWORD);
        postgresProperties.put(jdbcUsername(), USER_NAME);
        postgresProperties.put(jdbcUrl(), "jdbc:postgresql://localhost:5432/" + schemaName);
        postgresProperties.put(jdbcAutocreateTable(), "true");
        postgresProperties.put(jdbcAutocreateDatabase(), "jdbc:postgresql://localhost:5432/postgres");
        return  postgresProperties;
    }

    public static Properties postgresRepositoryConfigSerializable(String schemaName) {
        var postgresProperties = new Properties();
        postgresProperties.put(jdbcDriver(), "org.postgresql.Driver");
        postgresProperties.put(jdbcPassword(), USER_PASSWORD);
        postgresProperties.put(jdbcUsername(), USER_NAME);
        postgresProperties.put(jdbcUrl(), "jdbc:postgresql://localhost:5432/" + schemaName);
        postgresProperties.put(jdbcAutocreateTable(), "true");
        postgresProperties.put(jdbcAutocreateDatabase(), "jdbc:postgresql://localhost:5432/postgres");
        postgresProperties.put(jdbcTransactionIsolationLevel(), "serializable");
        return  postgresProperties;
    }

    public static Properties postgresRepositoryConfigRepeatableRead(String schemaName) {
        var postgresProperties = new Properties();
        postgresProperties.put(jdbcDriver(), "org.postgresql.Driver");
        postgresProperties.put(jdbcPassword(), USER_PASSWORD);
        postgresProperties.put(jdbcUsername(), USER_NAME);
        postgresProperties.put(jdbcUrl(), "jdbc:postgresql://localhost:5432/" + schemaName);
        postgresProperties.put(jdbcAutocreateTable(), "true");
        postgresProperties.put(jdbcAutocreateDatabase(), "jdbc:postgresql://localhost:5432/postgres");
        postgresProperties.put(jdbcTransactionIsolationLevel(), "repeatable-read");
        return  postgresProperties;
    }


    public static Properties h2RepositoryConfig() {
        var h2Properties = new Properties();
        h2Properties.put(jdbcDriver(), "org.h2.Driver");
        h2Properties.put(jdbcPassword(), USER_PASSWORD);
        h2Properties.put(jdbcUsername(), USER_NAME);
        h2Properties.put(jdbcUrl(), "jdbc:h2:mem:jexxa;DB_CLOSE_DELAY=-1");
        h2Properties.put(jdbcAutocreateTable(), "true");

        return h2Properties;
    }
    public static Properties s3RepositoryConfig() {
        var s3Properties = new Properties();
        s3Properties.put(s3Endpoint(), "http://localhost:8100");
        s3Properties.put(s3Bucket(), "jexxa-adapters-test");
        s3Properties.put(s3SecretKey(), "minioadmin");
        s3Properties.put(s3AccessKey(), "minioadmin");
        return s3Properties;
    }


    public static Properties imdbRepositoryConfig()
    {
        return new Properties();
    }

}
