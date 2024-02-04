package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.plugins.generic.GenericProducer;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static io.jexxa.jlegmed.plugins.generic.producer.ScheduledProducer.schedule;
import static io.jexxa.jlegmed.plugins.generic.producer.ScheduledProducer.scheduledProducer;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        var messageCollector = new Stack<String>();
        var message = "Hello World";

        jlegmed.newFlowGraph("HelloWorld")
                .every(10, MILLISECONDS)
                .receive(String.class).from(() -> message)

                .and().processWith( GenericProcessors::idProcessor )
                .and().consumeWith( messageCollector::push );
        //Act
        jlegmed.start();

        //Assert - We expect at least three messages that must be the string in 'message'
        await().atMost(3, SECONDS).until(() -> messageCollector.size() >= 3);

        assertEquals(message, messageCollector.toArray()[0]);
        assertEquals(message, messageCollector.toArray()[1]);
        assertEquals(message, messageCollector.toArray()[2]);
    }

    @Test
    void testRepeatHelloWorld() {
        //Arrange
        var messageCollector = new Stack<String>();
        var message = "Hello World";

        jlegmed.newFlowGraph("RepeatHelloWorld")
                .repeat(3)
                .receive(String.class).from(() -> message)

                .and().processWith( GenericProcessors::idProcessor )
                .and().consumeWith( messageCollector::push );

        //Act
        jlegmed.start();

        //Assert - We expect exactly three messages that must be the string in 'message'
        await().atMost(3, SECONDS).until(() -> messageCollector.size() == 3);

        assertEquals(message, messageCollector.toArray()[0]);
        assertEquals(message, messageCollector.toArray()[1]);
        assertEquals(message, messageCollector.toArray()[2]);
    }

    @Test
    void testtestRepeatAtIntervalHelloWorld() {
        //Arrange
        var messageCollector = new Stack<String>();
        var message = "Hello World";

        jlegmed.newFlowGraph("RepeatAtIntervalHelloWorld")
                .repeat(3).atInterval(10, MILLISECONDS)

                .receive(String.class).from(() -> message)

                .and().processWith( GenericProcessors::idProcessor )
                .and().processWith( GenericProcessors::consoleLogger )
                .and().consumeWith( messageCollector::push );

        //Act
        jlegmed.start();

        //Assert - We expect exactly three messages that must be the string in 'message'
        await().atMost(10, SECONDS).until(() -> messageCollector.size() == 3);

        assertEquals(message, messageCollector.toArray()[0]);
        assertEquals(message, messageCollector.toArray()[1]);
        assertEquals(message, messageCollector.toArray()[2]);
    }

    @Test
    void testAwaitHelloWorld() {
        //Arrange
        var messageCollector = new Stack<String>();

        jlegmed.newFlowGraph("AwaitHelloWorld")
                .await(String.class)
                .from(() -> scheduledProducer(() -> "HelloWorld", schedule(50, MILLISECONDS)))
                
                //Here we configure a processor that uses FilterContext to skip the second message
                .and().processWith( GenericProcessors::idProcessor )
                .and().consumeWith( messageCollector::push );
        //Act
        jlegmed.start();

        //Assert - That each second message is skipped
        await().atMost(3, SECONDS).until(() -> messageCollector.size() >= 3);
        assertEquals("HelloWorld", messageCollector.toArray()[0]);
        assertEquals("HelloWorld", messageCollector.toArray()[1]);
        assertEquals("HelloWorld", messageCollector.toArray()[2]);
    }

    @Test
    void testChangeData() {
        //Arrange
        var messageCollector = new Stack<String>();
        var inputData = "Hello World";
        var expectedResult  = inputData + "-" + inputData;

        jlegmed.newFlowGraph("ChangeData")
                .every(10, MILLISECONDS)
                .receive(String.class).from(() -> inputData)

                .and().processWith( data -> data + "-" + data )
                .and().consumeWith( messageCollector::push );
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.size() >= 3);
        assertEquals(expectedResult, messageCollector.toArray()[0]);
    }

    @Test
    void testMultipleFlowGraphs() {
        //Arrange
        var messageCollector1 = new Stack<Integer>();
        var messageCollector2 = new Stack<Integer>();

        jlegmed.newFlowGraph("FlowGraph1")
                .every(10, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)

                .and().processWith(GenericProcessors::idProcessor)
                .and().consumeWith( messageCollector1::push );


        jlegmed.newFlowGraph("FlowGraph2")
                .every(20, MILLISECONDS)
                .receive(Integer.class).from(GenericProducer::counter)

                .and().processWith(GenericProcessors::idProcessor)
                .and().consumeWith( messageCollector2::push );

        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector1.size() >= 3);
        await().atMost(3, SECONDS).until(() -> messageCollector2.size() >= 3);
    }

}
