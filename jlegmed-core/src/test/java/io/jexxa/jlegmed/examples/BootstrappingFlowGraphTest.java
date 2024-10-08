package io.jexxa.jlegmed.examples;

import io.jexxa.jlegmed.core.JLegMed;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

import static io.jexxa.jlegmed.examples.TestFilter.NewContract.newContract;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BootstrappingFlowGraphTest {
    private static JLegMed jlegmed;

    @BeforeEach
    void initBeforeEach()
    {
        jlegmed = new JLegMed(BootstrappingFlowGraphTest.class)
                .disableStrictFailFast() // For testing purposes, we disable fail fast
                .disableBanner();
    }

    @AfterEach
    void deInitAfterEach()
    {
        jlegmed.stop();
    }
    @Test
    void testChangeDataType() {
        //Arrange
        var dataSource = new Stack<TestFilter.NewContract>();
        var dataSink = new Stack<TestFilter.NewContract>();
        var counter = new AtomicInteger(1);
        var repeatCounter = 10;

        //First, we have to initialize our data source before some other flow graph can start processing
        jlegmed.bootstrapFlowGraph("Setup Contract").repeat(repeatCounter)
                .receive(TestFilter.NewContract.class)
                .from(() -> newContract(counter.getAndIncrement()))
                .and().consumeWith(dataSource::push);

        jlegmed.newFlowGraph("Process contracts")
                .repeat(repeatCounter)
                .receive(TestFilter.NewContract.class).from(dataSource::pop)
                .and().consumeWith( dataSink::push );
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> dataSink.size() == repeatCounter);
        assertTrue( dataSource.isEmpty());
    }
}
