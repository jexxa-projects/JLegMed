package io.jexxa.jlegmed.plugins.persistence;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.MessageCollector;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import io.jexxa.jlegmed.plugins.persistence.processor.AbstractAggregate;
import io.jexxa.jlegmed.plugins.persistence.processor.RepositoryProcessor;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Function;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class PersistenceFlowGraphIT {

    @Test
    void testFlowGraph() {
        //Arrange
        var messageCollector = new MessageCollector<TextEntity>();
        var jlegmed = new JLegMed(PersistenceFlowGraphIT.class);
        jlegmed.newFlowGraph("HelloWorld")
                .each(10, MILLISECONDS)
                .receive(String.class).from(() -> "Hello World")

                .andProcessWith( data -> new TextEntity(data) )
                .andProcessWith( RepositoryProcessor::persist ).useProperties("test-jdbc-connection")
                .andProcessWith( GenericProcessors::consoleLogger )
                .andProcessWith( messageCollector::collect );
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        jlegmed.stop();
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
