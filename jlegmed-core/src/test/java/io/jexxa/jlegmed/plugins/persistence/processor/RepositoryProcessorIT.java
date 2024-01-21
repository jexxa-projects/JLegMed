package io.jexxa.jlegmed.plugins.persistence.processor;

import io.jexxa.common.facade.utils.properties.PropertiesUtils;
import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.processor.GenericCollector;
import io.jexxa.jlegmed.plugins.persistence.TestData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Function;

import static io.jexxa.jlegmed.core.filter.FilterProperties.filterPropertiesOf;
import static io.jexxa.jlegmed.plugins.persistence.JDBCOperation.dropTable;
import static io.jexxa.jlegmed.plugins.persistence.processor.JDBCProcessor.jdbcExecutor;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class RepositoryProcessorIT {

    private static JLegMed jLegMed;

    @BeforeEach
    void init() {
        jLegMed = new JLegMed(RepositoryProcessorIT.class).disableBanner();

        //Drop existing table
        var properties = PropertiesUtils.getSubset(jLegMed.getProperties(), "test-jdbc-connection");

        jdbcExecutor(dropTable(TestData.class))
                .useProperties(filterPropertiesOf("test-jdbc-connection", properties))
                .reachStarted()
                .reachDeInit();
    }

    @AfterEach
    void deInit() {
        if (jLegMed != null)
        {
            jLegMed.stop();
        }
    }

    @Test
    void testFlowGraph() {
        //Arrange
        var messageCollector = new GenericCollector<TextEntity>();

        jLegMed.newFlowGraph("HelloWorld")

                .every(10, MILLISECONDS)
                .receive(String.class).from(() -> "Hello World")

                .and().processWith( data -> new TextEntity(data) )
                .and().processWith( RepositoryProcessor::persist ).useProperties("test-jdbc-connection")
                .and().consumeWith( messageCollector::collect );
        //Act
        jLegMed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
    }


    private static class TextEntity implements AbstractAggregate<TextEntity, String>
    {
        private final String data;
        private final String aggregateID;
        public TextEntity(String data, String aggregateID)
        {
            this.aggregateID = aggregateID;
            this.data = data;
        }

        public TextEntity(String data)
        {
            this(data, UUID.randomUUID().toString());
        }


        public String getAggregateID() {
            return aggregateID;
        }

        @Override
        public Class<TextEntity> getAggregateType() {
            return TextEntity.class;
        }

        @Override
        public Function<TextEntity, String> getKeyFunction() {
            return TextEntity::getAggregateID;
        }

        @Override
        public String toString()
        {
            return aggregateID + ":" + data;
        }
    }

}
