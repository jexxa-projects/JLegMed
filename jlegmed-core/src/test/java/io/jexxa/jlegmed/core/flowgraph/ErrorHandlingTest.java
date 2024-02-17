package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.core.filter.ProcessingError;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static io.jexxa.jlegmed.plugins.monitor.LogMonitor.logFunctionStyle;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ErrorHandlingTest {
    private static JLegMed jlegmed;
    private static final List<String> errorList = new ArrayList<>();


    @BeforeEach
    void initBeforeEach()
    {
        jlegmed = new JLegMed(AwaitFlowGraphTest.class).disableBanner();
        errorList.clear();
    }

    @AfterEach
    void deInitAfterEach()
    {
        jlegmed.stop();
    }

    @Test
    void testErrorHandling() {
        //Arrange
        var flowGraphID = "ReceiveHelloWorld";
        var result = new ArrayList<String>();

        // Define the flow graph:
        jlegmed.newFlowGraph(flowGraphID)
                //Using 'every'-statement ensures that the producer is triggered at the specified rate
                .every(500, MILLISECONDS)

                // We start with "Hello ", extend it with "World" and store the result in a list
                .receive(String.class).from(() -> "Hello ").onError(ErrorHandlingTest::errorHandler)

                .and().processWith( ErrorHandlingTest::throwRuntimeException).onError(ErrorHandlingTest::errorHandler)

                .and().consumeWith( data -> result.add(data) ).onError(ErrorHandlingTest::errorHandler);

        // For better understanding, we log the data flow
        jlegmed.monitorPipes(flowGraphID, logFunctionStyle());

        //Act
        jlegmed.start();

        //Assert - We expect at least three messages that must be the string in 'message'
        await().atMost(3, SECONDS).until(() -> errorList.size() >= 3);
        assertTrue(result.isEmpty());
    }

    static String throwRuntimeException(String message)
    {
        if (message.equals("Hello ")) {
            throw new RuntimeException("Test Exception ");
        }
        return message;
    }
    public static void errorHandler(ProcessingError<String> processingError)
    {
        System.out.println("Could not process message " + processingError.originalMessage());
        errorList.add(processingError.originalMessage());
    }
}
