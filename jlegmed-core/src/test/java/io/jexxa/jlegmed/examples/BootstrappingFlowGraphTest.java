package io.jexxa.jlegmed.examples;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.examples.contract.NewContract;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static io.jexxa.jlegmed.examples.plugins.ContractSteps.contractGenerator;
import static io.jexxa.jlegmed.examples.plugins.ContractSteps.readContract;
import static io.jexxa.jlegmed.examples.plugins.ContractSteps.storeContract;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BootstrappingFlowGraphTest {
    private static JLegMed jlegmed;

    @BeforeEach
    void initBeforeEach()
    {
        jlegmed = new JLegMed(BootstrappingFlowGraphTest.class)
                .disableBanner();
    }

    @AfterEach
    void deInitAfterEach()
    {
        jlegmed.stop();
    }
    @Test
    void testChangeDataType() {
        //Arrange
        var dataStorage = new Stack<NewContract>();
        var results = new Stack<NewContract>();
        var repeatCounter = 10;

        //First, we have to initialize our data source before some other flow graph can start processing
        jlegmed.bootstrapFlowGraph("Setup Contract").repeat(repeatCounter)
                .receive(NewContract.class)
                .from(contractGenerator)
                .then().sinkTo(storeContract(dataStorage));

        jlegmed.newFlowGraph("Process contracts")
                .repeat(repeatCounter)
                .receive(NewContract.class).from(readContract(dataStorage))

                .then().sinkTo( storeContract(results) );
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> results.size() == repeatCounter);
        assertTrue( dataStorage.isEmpty());
        jlegmed.stop();
    }
}
