package io.jexxa.jlegmed.examples;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.core.filter.ProcessingError;
import io.jexxa.jlegmed.core.filter.ProcessingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static io.jexxa.common.facade.logger.SLF4jLogger.getLogger;
import static io.jexxa.jlegmed.plugins.generic.producer.OnErrorProducer.onErrorProducer;
import static io.jexxa.jlegmed.plugins.monitor.LogMonitor.logFunctionStyle;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ErrorHandlingTest {
    private static JLegMed jlegmed;
    private static final List<String> errorList = new ArrayList<>();


    @BeforeEach
    void initBeforeEach()
    {
        jlegmed = new JLegMed(ErrorHandlingTest.class).disableBanner();
        errorList.clear();
    }

    @AfterEach
    void deInitAfterEach()
    {
        jlegmed.stop();
    }

    @Test
    void testCollectProcessingErrors() {
        //Arrange
        var flowGraphID = "ReceiveHelloWorld";
        var result = new ArrayList<String>();

        // Define the flow graph:
        jlegmed.newFlowGraph(flowGraphID)
                //Using 'every'-statement ensures that the producer is triggered at the specified rate
                .every(500, MILLISECONDS)

                // We start with "Hello ", extend it with "World" and store the result in a list
                .receive(String.class).from(() -> "Hello ").onError(ErrorHandlingTest::collectProcessingErrors)

                .and().processWith( message-> message + "World")

                .and().processWith( ErrorHandlingTest::throwRuntimeExceptionIfMessageHelloWorld)

                .and().consumeWith( data -> result.add(data) ).onError(ErrorHandlingTest::collectProcessingErrors);

        // For better understanding, we log the data flow
        jlegmed.monitorPipes(flowGraphID, logFunctionStyle());

        //Act
        jlegmed.start();

        //Assert - We expect at least three messages that must be the string in 'message'
        await().atMost(3, SECONDS).until(() -> errorList.size() >= 3);
        assertTrue(result.isEmpty());
    }

    @Test
    void testConnectedErrorPipe() {
        //Arrange
        var flowGraphID = "ReceiveHelloWorld";
        var errorHandler = onErrorProducer(ErrorMessage::new);
        var result = new Stack<ErrorMessage>();

        // Define the flow graph:
        jlegmed.newFlowGraph(flowGraphID)
                //Using 'every'-statement ensures that the producer is triggered at the specified rate
                .every(500, MILLISECONDS)

                // We start with "Hello ", extend it with "World" and store the result in a list
                .receive(String.class).from(() -> "Hello ").onError(errorHandler::notify)
                .and().processWith( message-> message + "World")
                .and().consumeWith(ErrorHandlingTest::throwRuntimeExceptionIfMessageHelloWorld);


        //Act - Define the flow graph for error handling
        jlegmed.newFlowGraph("Error handling flow graph")
                .await(ErrorMessage.class).from(errorHandler)
                .and().processWith(result::push)
                .and().consumeWith(errorMessage ->
                            getLogger(ErrorHandlingTest.class).warn("{} {}", errorMessage.data() , errorMessage.processingException().getMessage())
                );


        jlegmed.start();

        //Assert - We expect at least three messages that must be the string in 'message'
        await().atMost(3, SECONDS).until(() -> result.size() >= 3);
    }


    @Test
    void testUnconnectedErrorPipe() {
        //Arrange
        var flowGraphID = "ReceiveHelloWorld";

        // Define the flow graph:
        jlegmed.newFlowGraph(flowGraphID)
                //Using 'every'-statement ensures that the producer is triggered at the specified rate
                .every(500, MILLISECONDS)

                // We start with "Hello ", extend it with "World" and store the result in a list
                .receive(String.class).from(() -> throwRuntimeExceptionIfMessageHelloWorld("Hello World"))
                .and().consumeWith(errorMessage -> getLogger(ErrorHandlingTest.class).warn(errorMessage));

        jlegmed.start();

        //Assert - We expect at least three messages that must be the string in 'message'
        await().atMost(3, SECONDS).until(() -> jlegmed.getFlowGraph(flowGraphID).processingStats().unhandledProcessingErrors().compareTo(BigInteger.valueOf(3)) > 0);
        assertEquals(BigInteger.valueOf(0), jlegmed.getFlowGraph(flowGraphID).processingStats().forwardedMessages());


    }

    record ErrorMessage (String data, ProcessingException processingException){ }

    static String throwRuntimeExceptionIfMessageHelloWorld(String message)
    {
        if (message.equals("Hello World")) {
            throw new RuntimeException("Test Exception ");
        }
        return message;
    }
    public static void collectProcessingErrors(ProcessingError<String> processingError)
    {
        getLogger(ErrorHandlingTest.class).warn("Filter `{}` could not process initial message `{}`", processingError.processingException().causedFilter(), processingError.originalMessage());
        errorList.add(processingError.originalMessage());
    }
}
