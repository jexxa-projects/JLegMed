package io.jexxa.jlegmed;

import io.jexxa.jlegmed.asyncreceive.dto.incoming.NewContract;
import io.jexxa.jlegmed.processor.ConsoleProcessor;
import io.jexxa.jlegmed.producer.GenericProducer;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

class JLegMedTest {

    @Test
    void test() {
        var jlegmed = new JLegMed();
        jlegmed.
                receive(NewContract.class).with(GenericProducer.class).andProcessWith(ConsoleProcessor.class)
                .run(10);
    }
}