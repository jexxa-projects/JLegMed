package io.jexxa.jlegmed.plugins.persistence.repository;

import io.jexxa.jlegmed.core.JLegMed;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;
import java.util.UUID;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RepositoryIT {

    private static JLegMed jLegMed;

    @BeforeEach
    void init() {
        jLegMed = new JLegMed(RepositoryIT.class)
                .useTechnology(RepositoryPool.class)
                .disableBanner();
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
        var messageCollector = new Stack<TextEntity>();

        jLegMed.bootstrapFlowGraph("reset database")
                .execute(TestRepository::dropTable);


        jLegMed.newFlowGraph("HelloWorld")
                .every(10, MILLISECONDS)
                .receive(String.class).from(() -> "Hello World")

                .and().processWith( data -> new TextEntity(data, UUID.randomUUID().toString()) )
                .and().processWith( TestRepository::add )
                .and().consumeWith( messageCollector::push );
        //Act
        jLegMed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.size() >= 3);
    }


    @Test
    void readData() {
        //Arrange
        var messageCollector = new Stack<TextEntity>();
        var numberOfData = 10;
        bootstrapTestData(jLegMed, numberOfData);

        jLegMed.newFlowGraph("Read Data")
                .repeat(1)
                .receive(TextEntity.class).from(TestRepository::read)
                .and().processWith( messageCollector::push );

        //Act
        jLegMed.start();
        await().atMost(3, SECONDS).until(jLegMed::waitUntilFinished );

        //Assert
        assertEquals(numberOfData, messageCollector.size());
    }

    private void bootstrapTestData(JLegMed jLegMed, int numberOfData) {
        jLegMed.bootstrapFlowGraph("reset database")
                .execute(TestRepository::dropTable);

        jLegMed.bootstrapFlowGraph("Init test data")
                .repeat(numberOfData)
                .receive(String.class).from(() -> "Hello World")

                .and().processWith( data -> new TextEntity(data, UUID.randomUUID().toString()) )
                .and().consumeWith( TestRepository::add);
    }


}
