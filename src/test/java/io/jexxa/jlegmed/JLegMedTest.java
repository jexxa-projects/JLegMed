package io.jexxa.jlegmed;

import io.jexxa.jlegmed.asyncreceive.dto.incoming.NewContract;
import io.jexxa.jlegmed.jexxacp.common.wrapper.jdbc.JDBCConnection;
import io.jexxa.jlegmed.processor.ConsoleProcessor;
import io.jexxa.jlegmed.producer.GenericProducer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.util.Properties;

import static java.util.concurrent.TimeUnit.SECONDS;

class JLegMedTest {

    @Test
    void testAwait() {
        var jlegmed = new JLegMed();
        jlegmed
                .await(NewContract.class).from(GenericProducer.class)
                .andProcessWith(ConsoleProcessor.class)

                .start();

        jlegmed.stop();
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

        jlegmed.stop();
    }
    @Test
    void testEach() throws InterruptedException {
        var jlegmed = new JLegMed();
        jlegmed
                .each(1, SECONDS)
                .receive(NewContract.class).from(GenericProducer.class)
                .andProcessWith(ConsoleProcessor.class)

                .start();

        //replace with await
        Thread.sleep(3000);

        jlegmed.stop();
    }

    @Test
    @Disabled("Currently not implemented")
    void testURLEach() throws InterruptedException {
        var jlegmed = new JLegMed();
        jlegmed
                .each(1, SECONDS)
                .receive(NewContract.class).from("jdbc://MYDataBase").with(this::readData)
                .andProcessWith(ConsoleProcessor.class)

                .start();

        //replace with await
        Thread.sleep(3000);

        jlegmed.stop();
    }

    @Test
    void testMultipleEach() throws InterruptedException {
        var jlegmed = new JLegMed();
        jlegmed
                .each(1, SECONDS).receive(NewContract.class).from(GenericProducer.class)
                .andProcessWith(ConsoleProcessor.class)

                .each(2, SECONDS).receive(NewContract.class).from(GenericProducer.class)
                .andProcessWith(ConsoleProcessor.class)

                .start();

        //replace with await
        Thread.sleep(3000);

        jlegmed.stop();
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

        jlegmed.stop();
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