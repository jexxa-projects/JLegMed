package io.jexxa.jlegmed.plugins.generic.producer;

import com.google.gson.Gson;
import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.examples.ContractFilter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Stack;

import static io.jexxa.jlegmed.examples.ContractFilter.NewContract.newContract;
import static io.jexxa.jlegmed.examples.ContractSteps.passthroughContract;
import static io.jexxa.jlegmed.examples.ContractSteps.storeContract;
import static io.jexxa.jlegmed.examples.ContractSteps.updateContract;
import static io.jexxa.jlegmed.plugins.generic.producer.JSONReader.ProducerMode.ONLY_ONCE;
import static io.jexxa.jlegmed.plugins.generic.producer.JSONReader.ProducerMode.UNTIL_STOPPED;
import static io.jexxa.jlegmed.plugins.generic.producer.JSONReader.jsonStream;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JSONReaderTest {
    private static JLegMed jlegmed;

    @BeforeEach
    void initBeforeEach()
    {
        jlegmed = new JLegMed(JSONReaderTest.class).disableBanner();
    }

    @AfterEach
    void deInitAfterEach()
    {
        jlegmed.stop();
    }

    @Test
    void testFactoryMethodUntilStopped() {
        //Arrange
        var contractStorage = new Stack<ContractFilter.UpdatedContract>();
        var inputStream = new ByteArrayInputStream(new Gson().toJson(newContract(1)).getBytes());

        jlegmed.newFlowGraph("testFactoryMethodUntilStopped")

                .every(10, MILLISECONDS)

                // Here we configure a producer using a factory method telling us the configuration mode
                .receive(ContractFilter.NewContract.class)
                .from(jsonStream(inputStream, UNTIL_STOPPED))
                .then().processWith(updateContract)

                .then().processWith( passthroughContract )
                .then().sinkTo( storeContract(contractStorage) );
        //Act
        jlegmed.start();

        //Assert - We must receive > 1 message
        await().atMost(3, SECONDS).until(() -> contractStorage.size() > 1);
    }


    @Test
    void testFactoryMethodOnlyOnce() {
        //Arrange
        var messageCollector = new Stack<ContractFilter.UpdatedContract>();
        var inputStream = new ByteArrayInputStream(new Gson().toJson(newContract(1)).getBytes());

        jlegmed.newFlowGraph("FilterConfigOnlyOnce")
                .every(10, MILLISECONDS)
                .receive(ContractFilter.NewContract.class)

                // Here we configure a producer using a factory method telling us the configuration mode
                .from(jsonStream(inputStream, ONLY_ONCE))
                .then().processWith(updateContract)

                .then().processWith( passthroughContract )
                .then().sinkTo( storeContract(messageCollector) );
        //Act
        jlegmed.start();

        //Assert - We receive only a single message
        await().atMost(3, SECONDS).until(() -> messageCollector.size() == 1);
        assertEquals(1, messageCollector.size());
    }


}
