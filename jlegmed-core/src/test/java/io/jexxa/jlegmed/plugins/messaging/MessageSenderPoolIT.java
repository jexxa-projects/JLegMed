package io.jexxa.jlegmed.plugins.messaging;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.core.filter.FilterContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.jexxa.jlegmed.plugins.messaging.MessageSenderPool.getMessageSender;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MessageSenderPoolIT {
    private static JLegMed jLegMed;
    @BeforeEach
    void init() {
        jLegMed = new JLegMed(MessageSenderPoolIT.class).disableBanner();
    }

    @AfterEach
    void deInit() {
        jLegMed.stop();
    }

    @Test
    void failFastInvalidProperties() {
        //Arrange
        MessageSenderPool.init();

        jLegMed.newFlowGraph("HelloWorld")
                .every(10, MILLISECONDS)
                .receive(String.class).from(() -> "Hello World")

                .and().consumeWith( MessageSenderPoolIT::myQueue ).useProperties("invalid-factory-jms-connection");

        //Act/Assert
        assertThrows(IllegalArgumentException.class, () -> jLegMed.start());
    }

    public static <T> void myQueue(T data, FilterContext filterContext)
    {
        getMessageSender(filterContext)
                .send(data)
                .addHeader("Type", data.getClass().getSimpleName())
                .toQueue("MyQueue")
                .asJson();
    }
}
