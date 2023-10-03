package io.jexxa.jlegmed;

import io.jexxa.jlegmed.asyncreceive.dto.incoming.NewContract;
import io.jexxa.jlegmed.processor.ConsoleProcessor;
import io.jexxa.jlegmed.producer.GenericProducer;
import org.junit.jupiter.api.Test;

import static java.util.concurrent.TimeUnit.SECONDS;

class JLegMedTest {

    @Test
    void testAwait() {
        var jlegmed = new JLegMed();
        jlegmed.
                await(NewContract.class).from(GenericProducer.class).andProcessWith(ConsoleProcessor.class)
                .start();

        jlegmed.stop();
    }

    @Test
    void testEach() throws InterruptedException {
        var jlegmed = new JLegMed();
        jlegmed
                .each(1, SECONDS).receive(NewContract.class).from(GenericProducer.class)
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

}