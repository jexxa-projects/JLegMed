package io.jexxa.jlegmed;

import io.jexxa.jlegmed.asyncreceive.dto.incoming.NewContract;
import io.jexxa.jlegmed.asyncreceive.dto.incoming.UpdatedContract;
import io.jexxa.jlegmed.processor.ConsoleProcessor;
import io.jexxa.jlegmed.processor.IDProcessor;
import io.jexxa.jlegmed.processor.MessageCollector;
import io.jexxa.jlegmed.producer.GenericProducer;
import org.junit.jupiter.api.Test;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class EachFlowGraphTest {
    @Test
    void testSingleFlowGraph() {
        //Arrange
        var messageCollector = new MessageCollector();
        var jlegmed = new JLegMed();
        jlegmed
                .each(10, MILLISECONDS)
                .receive(NewContract.class).from(GenericProducer.class)
                .andProcessWith(IDProcessor.class )
                .andProcessWith(ConsoleProcessor.class)
                .andProcessWith(messageCollector);
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        jlegmed.stop();
    }


    @Test
    void testMultipleFlowGraphs() {
        //Arrange
        var messageCollector1 = new MessageCollector();
        var messageCollector2 = new MessageCollector();

        var jlegmed = new JLegMed();
        jlegmed
                .each(10, MILLISECONDS).receive(NewContract.class).from(GenericProducer.class)
                .andProcessWith(ConsoleProcessor.class)
                .andProcessWith(messageCollector1)

                .each(10, MILLISECONDS).receive(NewContract.class).from(GenericProducer.class)
                .andProcessWith(ConsoleProcessor.class)
                .andProcessWith(messageCollector2);

        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector1.getNumberOfReceivedMessages() >= 3);
        await().atMost(3, SECONDS).until(() -> messageCollector2.getNumberOfReceivedMessages() >= 3);

        jlegmed.stop();
    }

    @Test
    void testTransformData() {
        //Arrange
        var messageCollector = new MessageCollector();
        var jlegmed = new JLegMed();
        jlegmed
                .each(10, MILLISECONDS)
                .receive(NewContract.class).from(GenericProducer.class)
                .andProcessWith(MyTransformer::transformToUpdatedContract)
                .andProcessWith(ConsoleProcessor.class)
                .andProcessWith(messageCollector);
        //Act
        jlegmed.start();
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        jlegmed.stop();
    }

    public static class MyTransformer  {
        public static Message transformToUpdatedContract(Message message) {
            return new Message(new UpdatedContract(message.getData(NewContract.class).contractNumber(), "newInfo"));
        }
    }

}
