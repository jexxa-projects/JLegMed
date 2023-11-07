package io.jexxa.jlegmed.common.component.persistence.repository.jdbc;

import io.jexxa.jlegmed.common.wrapper.jdbc.JDBCProperties;
import io.jexxa.jlegmed.common.wrapper.jdbc.builder.JDBCObject;
import io.jexxa.jlegmed.common.wrapper.jdbc.database.DatabaseManager;
import io.jexxa.jlegmed.common.wrapper.jdbc.database.IDatabase;
import io.jexxa.jlegmed.common.component.persistence.repository.IRepository;
import org.slf4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

import static io.jexxa.jlegmed.common.wrapper.jdbc.builder.JDBCTableBuilder.SQLConstraint.PRIMARY_KEY;
import static io.jexxa.jlegmed.common.wrapper.jdbc.builder.SQLDataType.JSONB;
import static io.jexxa.jlegmed.common.wrapper.json.JSONManager.getJSONConverter;
import static io.jexxa.jlegmed.common.wrapper.logger.SLF4jLogger.getLogger;

public class JDBCKeyValueRepository<T, K> extends JDBCRepository implements IRepository<T, K>
{
    private static final Logger LOGGER = getLogger(JDBCKeyValueRepository.class);

    private final Function<T,K> keyFunction;
    private final Class<T> aggregateClazz;
    private final IDatabase database;

    public enum KeyValueSchema
    {
        REPOSITORY_KEY,
        REPOSITORY_VALUE
    }

    @SuppressWarnings("unused")
    public JDBCKeyValueRepository(Class<T> aggregateClazz, Function<T,K> keyFunction, Properties properties)
    {
        super(properties);

        this.keyFunction = Objects.requireNonNull( keyFunction );
        this.aggregateClazz = Objects.requireNonNull(aggregateClazz);
        this.database = DatabaseManager.getDatabase(properties.getProperty(JDBCProperties.JDBC_URL));

        manageDBTable(properties);
    }

    protected JDBCKeyValueRepository(Class<T> aggregateClazz, Function<T,K> keyFunction, Properties properties, boolean manageTable)
    {
        super(properties);

        this.keyFunction = Objects.requireNonNull( keyFunction );
        this.aggregateClazz = Objects.requireNonNull(aggregateClazz);
        this.database = DatabaseManager.getDatabase(properties.getProperty(JDBCProperties.JDBC_URL));

        if ( manageTable )
        {
            manageDBTable(properties);
        }
    }


    @Override
    public void remove(K key)
    {
        Objects.requireNonNull(key);
        var jdbcKey = new JDBCObject(getJSONConverter().toJson(key), database.matchingValue(JSONB));

        var command = getConnection().createCommand(KeyValueSchema.class)
                .deleteFrom(aggregateClazz)
                .where(KeyValueSchema.REPOSITORY_KEY)
                .isEqual(jdbcKey)
                .create();

        command.asUpdate();
    }

    @Override
    public void removeAll()
    {
        var command = getConnection().createCommand(KeyValueSchema.class)
                .deleteFrom(aggregateClazz)
                .create();

        command.asIgnore();
    }

    @Override
    public void add(T aggregate)
    {
        Objects.requireNonNull(aggregate);

        var command = getConnection().createCommand(KeyValueSchema.class)
                .insertInto(aggregateClazz)
                .values(new JDBCObject[]{
                        primaryKeyToJSONB(keyFunction.apply(aggregate)),
                        valueToJSONB(aggregate)}
                )
                .create();

        command.asUpdate();
    }

    @SuppressWarnings({"unused"})
    @Override
    public void update(T aggregate)
    {
        Objects.requireNonNull(aggregate);

        var command = getConnection().createCommand(KeyValueSchema.class)
                .update(aggregateClazz)
                .set(KeyValueSchema.REPOSITORY_VALUE, valueToJSONB(aggregate))
                .where(KeyValueSchema.REPOSITORY_KEY)
                .isEqual(primaryKeyToJSONB(keyFunction.apply(aggregate)))
                .create();

        command.asUpdate();
    }

    @Override
    public Optional<T> get(K primaryKey)
    {
        Objects.requireNonNull(primaryKey);

        var query = getConnection().createQuery(KeyValueSchema.class)
                .select(KeyValueSchema.REPOSITORY_VALUE)
                .from(aggregateClazz)
                .where(KeyValueSchema.REPOSITORY_KEY)
                .isEqual(primaryKeyToJSONB(primaryKey))
                .create();

        return  query
                .asString()
                .flatMap(Optional::stream)
                .findFirst()
                .map( element -> getJSONConverter().fromJson(element, aggregateClazz))
                .or(Optional::empty);
    }

    @Override
    public List<T> get()
    {
        var query = getConnection().createQuery(KeyValueSchema.class)
                .select(KeyValueSchema.REPOSITORY_VALUE)
                .from(aggregateClazz)
                .create();

        return query
                .asString()
                .flatMap(Optional::stream)
                .map( element -> getJSONConverter().fromJson(element, aggregateClazz))
                .toList();
    }

    private void manageDBTable(Properties properties)
    {
        if (properties.containsKey(JDBCProperties.JDBC_AUTOCREATE_TABLE))
        {
            autocreateTableKeyValue();
            renameKeyValueColumns();
            alterKeyValueRows();
        }
    }

    private void autocreateTableKeyValue()
    {
        try{

            var command = getConnection().createTableCommand(KeyValueSchema.class)
                    .createTableIfNotExists(aggregateClazz)
                    .addColumn(KeyValueSchema.REPOSITORY_KEY, database.matchingPrimaryKey(JSONB))
                    .addConstraint(PRIMARY_KEY)
                    .addColumn(KeyValueSchema.REPOSITORY_VALUE, database.matchingValue(JSONB))
                    .create();

            command.asIgnore();
        }
        catch (RuntimeException e)
        {
            LOGGER.debug("Could not create table {} => Assume that table already exists", aggregateClazz.getSimpleName());
        }
    }

    protected final void alterKeyValueRows()
    {
        database.alterColumnType(getConnection(), aggregateClazz, KeyValueSchema.REPOSITORY_KEY.name(), database.matchingPrimaryKey(JSONB));

        database.alterColumnType(getConnection(), aggregateClazz, KeyValueSchema.REPOSITORY_VALUE.name(), database.matchingValue(JSONB));
    }

    protected final void renameKeyValueColumns()
    {
        if (database.columnExist(getConnection(), aggregateClazz.getSimpleName(), "key"))
        {
            database.renameColumn(getConnection(), aggregateClazz.getSimpleName(), "key", KeyValueSchema.REPOSITORY_KEY.name());
        }

        if (database.columnExist(getConnection(), aggregateClazz.getSimpleName(), "value"))
        {
            database.renameColumn(getConnection(), aggregateClazz.getSimpleName(), "value", KeyValueSchema.REPOSITORY_VALUE.name());
        }
    }


    protected JDBCObject primaryKeyToJSONB(Object value)
    {
        return new JDBCObject(getJSONConverter().toJson(value), database.matchingPrimaryKey(JSONB));
    }

    protected JDBCObject valueToJSONB(Object value)
    {
        return new JDBCObject(getJSONConverter().toJson(value), database.matchingValue(JSONB));
    }
}
