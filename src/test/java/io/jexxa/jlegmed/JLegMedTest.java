package io.jexxa.jlegmed;

import io.jexxa.jlegmed.asyncreceive.dto.incoming.NewContract;
import io.jexxa.jlegmed.jexxacp.common.wrapper.jdbc.JDBCConnection;
import io.jexxa.jlegmed.processor.ConsoleProcessor;
import io.jexxa.jlegmed.processor.IDProcessor;
import io.jexxa.jlegmed.processor.MessageCollector;
import io.jexxa.jlegmed.producer.GenericProducer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.util.Properties;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class JLegMedTest {

    @Test
    void testEach() {
        //Arrange
        var messageCollector = new MessageCollector();
        var jlegmed = new JLegMed();
        jlegmed
                .each(10, MILLISECONDS)
                .receive(NewContract.class).from(GenericProducer.class)
                .andProcessWith(ConsoleProcessor.class)
                .andProcessWith(messageCollector);
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        jlegmed.stop();
    }

    @Test
    void testEachMultipleProcessor() {
        //Arrange
        var messageCollector = new MessageCollector();

        var jlegmed = new JLegMed();
        jlegmed
                .each(10, MILLISECONDS)
                .receive(NewContract.class).from(GenericProducer.class)
                .andProcessWith( IDProcessor.class )
                .andProcessWith( IDProcessor.class )
                .andProcessWith(ConsoleProcessor.class)
                .andProcessWith(messageCollector);
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        jlegmed.stop();
    }

    @Test
    void testMultipleEach() {
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
    @Disabled("Currently not implemented")
    void testEachSend() {
        var jlegmed = new JLegMed();
        jlegmed
                .each(1, SECONDS)
                .receive(NewContract.class).from(GenericProducer.class)
            //    .andSendTo("MyTopic").with(this::sendData)

                .start();

        //replace with await
        await().pollDelay(3, SECONDS).until(() -> true);

        assertDoesNotThrow(jlegmed::stop);
    }

    @Test
    void testAwait() {
        var jlegmed = new JLegMed();
        jlegmed
                .await(NewContract.class).from(GenericProducer.class)
                .andProcessWith(ConsoleProcessor.class)

                .start();

        assertDoesNotThrow(jlegmed::stop);
    }


    @Test
    @Disabled("Currently not implemented")
    void testAwaitURL() {
        var jlegmed = new JLegMed();
        jlegmed
                .await(NewContract.class).from("jms://MYURL").with(this::readData)
                .andProcessWith(ConsoleProcessor.class)

                .await(NewContract.class).from("file://MYURL").with(this::readData)
                .andProcessWith(ConsoleProcessor.class)


                .start();

        assertDoesNotThrow(jlegmed::stop);
    }


    @Test
    @Disabled("Currently not implemented")
    void testURLEach()  {
        var jlegmed = new JLegMed();
        jlegmed
                .each(1, SECONDS)
                .receive(NewContract.class).from("jdbc://MYDataBase").with(this::readData)
                .andProcessWith(ConsoleProcessor.class)

                .start();

        //replace with await
        await().pollDelay(3, SECONDS).until(() -> true);

        assertDoesNotThrow(jlegmed::stop);
    }


    @Test
    void testMultipleAwait() {
        var jlegmed = new JLegMed();
        jlegmed
                .await(NewContract.class).from(GenericProducer.class)
                .andProcessWith(ConsoleProcessor.class)

                .await(NewContract.class).from(GenericProducer.class)
                .andProcessWith(ConsoleProcessor.class)

                .start();

        assertDoesNotThrow(jlegmed::stop);
    }

    private NewContract readData(Properties properties )
    {
        try( var jdbcConnection = new JDBCConnection(properties) ) {
            var result = jdbcConnection.createQuery(NewContractSchema.class).select(NewContractSchema.NEW_CONTRACT_SCHEMA).from(NewContractSchema.class).where(NewContractSchema.NEW_CONTRACT_SCHEMA2).isEqual(2).create();
            return result.as(this::readNewContract).findFirst().orElseThrow();
        }
    }


    NewContract readNewContract(ResultSet resultSet)

    {
        return null;
    }

    public enum NewContractSchema
    {
        NEW_CONTRACT_SCHEMA,
        NEW_CONTRACT_SCHEMA2
    }

}