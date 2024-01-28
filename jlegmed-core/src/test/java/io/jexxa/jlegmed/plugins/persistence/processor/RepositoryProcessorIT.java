package io.jexxa.jlegmed.plugins.persistence.processor;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.processor.GenericCollector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.jexxa.jlegmed.plugins.persistence.JDBCOperation.dropTable;
import static io.jexxa.jlegmed.plugins.persistence.processor.RepositoryPool.getRepository;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class RepositoryProcessorIT {

    private static JLegMed jLegMed;

    @BeforeEach
    void init() {
        jLegMed = new JLegMed(RepositoryProcessorIT.class).disableBanner();
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

        jLegMed.newFlowGraph("reset database")
                .repeat(1)

                .receive(TextEntity.class).from((filterContext) -> dropTable(filterContext, TextEntity.class)).useProperties("test-jdbc-connection");

        jLegMed.newFlowGraph("HelloWorld")

                .every(10, MILLISECONDS)
                .receive(String.class).from(() -> "Hello World")

                .and().processWith( data -> new TextEntity(data, UUID.randomUUID().toString()) )

                .and().processWith( (data, filterContext) ->
                        getRepository(TextEntity.class, TextEntity::key, filterContext).add(data))
                    .useProperties("test-jdbc-connection")

                .and().consumeWith( messageCollector::collect );
        //Act
        jLegMed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
    }

    private void initTestData() {
        jLegMed.newFlowGraph("reset database")
                .repeat(1)

                .receive(TextEntity.class).from((filterContext) -> dropTable(filterContext, TextEntity.class)).useProperties("test-jdbc-connection");

        jLegMed.newFlowGraph("Init test data")

                .repeat(10)
                .receive(String.class).from(() -> "Hello World")

                .and().processWith( data -> new TextEntity(data, UUID.randomUUID().toString()) )

                .and().processWith( (data, filterContext) ->
                        getRepository(TextEntity.class, TextEntity::key, filterContext).add(data))
                .useProperties("test-jdbc-connection");


        jLegMed.start();
        jLegMed.waitUntilFinished();
        jLegMed.stop();
    }



    private record TextEntity (String data, String key) { }

}
