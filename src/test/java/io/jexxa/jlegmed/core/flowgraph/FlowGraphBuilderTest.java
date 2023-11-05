package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.dto.NewContract;
import io.jexxa.jlegmed.dto.UpdatedContract;
import io.jexxa.jlegmed.plugins.generic.GenericProducer;
import io.jexxa.jlegmed.plugins.generic.processor.GenericCollector;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.jexxa.jlegmed.plugins.generic.producer.ScheduledProducer.activeProducer;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class FlowGraphBuilderTest {
    private static JLegMed jlegmed;

    @BeforeEach
    void initBeforeEach()
    {
        jlegmed = new JLegMed(FlowGraphBuilderTest.class).disableBanner();
    }

    @AfterEach
    void deInitAfterEach()
    {
        jlegmed.stop();
    }

    @Test
    void testHelloWorld() {
        //Arrange
        var messageCollector = new GenericCollector<String>();

        jlegmed.newFlowGraph("HelloWorld")
                .each(10, MILLISECONDS)
                .receive(String.class).from(() -> "Hello World")

                .and().processWith( GenericProcessors::idProcessor )
                .and().processWith( messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
    }


    @Test
    void testActiveFlowGraph() {
        //Arrange
        var messageCollector = new GenericCollector<Integer>();

        jlegmed.newFlowGraph("ActiveFlowGraph")
                .await(Integer.class)
                .from(activeProducer(GenericProducer::counter).withInterval(50, MILLISECONDS))

                //Here we configure a processor that uses FilterContext to skip the second message
                .and().processWith( TestFilter::skipEachSecondMessage )
                .and().processWith( messageCollector::collect );
        //Act
        jlegmed.start();

        //Assert - That each second message is skipped
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        assertEquals(1, messageCollector.getMessages().get(0));
        assertEquals(3, messageCollector.getMessages().get(1));
        assertEquals(5, messageCollector.getMessages().get(2));
    }
    @Test
    void testChangeData() {
        //Arrange
        var messageCollector = new GenericCollector<String>();
        var inputData = "Hello World";
        var expectedResult  = inputData + "-" + inputData;

        jlegmed.newFlowGraph("ChangeData")
                .each(10, MILLISECONDS)
                .receive(String.class).from(() -> inputData)

                .and().processWith( data -> data + "-" + data )
                .and().processWith( messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        assertEquals(expectedResult, messageCollector.getMessages().get(0));
    }

    @Test
    void testMultipleFlowGraphs() {
        //Arrange
        var messageCollector1 = new GenericCollector<Integer>();
        var messageCollector2 = new GenericCollector<Integer>();

        jlegmed.newFlowGraph("FlowGraph1")
                .each(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)

                .and().processWith(GenericProcessors::idProcessor)
                .and().processWith(messageCollector1::collect);


        jlegmed.newFlowGraph("FlowGraph2")
                .each(20, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)

                .and().processWith(GenericProcessors::idProcessor)
                .and().processWith(messageCollector2::collect);

        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector1.getNumberOfReceivedMessages() >= 3);
        await().atMost(3, SECONDS).until(() -> messageCollector2.getNumberOfReceivedMessages() >= 3);
    }

    @Test
    void testChangeDataType() {
        //Arrange
        var messageCollector = new GenericCollector<UpdatedContract>();
        jlegmed.newFlowGraph("ChangeDataType")
                .each(10, MILLISECONDS)

                .receive(NewContract.class).from(TestFilter::newContract)

                .and().processWith(TestFilter::transformToUpdatedContract)
                .and().processWith(GenericProcessors::idProcessor)
                .and().processWith(messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        assertFalse(messageCollector.getMessages().isEmpty());
    }


    @Test
    void testTransformDataWithFilterState() {
        //Arrange
        var messageCollector = new GenericCollector<UpdatedContract>();

        jlegmed.newFlowGraph("TransformDataWithFilterState")
                .each(10, MILLISECONDS)
                //TestFilter::newContract uses FilterContext to manage its state information
                .receive(NewContract.class).from(TestFilter::newContract)

                .and().processWith( TestFilter::contextTransformer)
                .and().processWith( messageCollector::collect);
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
    }
}
