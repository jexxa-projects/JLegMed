package io.jexxa.jlegmed.examples;

import io.jexxa.jlegmed.core.JLegMed;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static io.jexxa.jlegmed.examples.ContractSteps.storeContract;
import static io.jexxa.jlegmed.examples.ContractSteps.contractGenerator;
import static io.jexxa.jlegmed.examples.ContractSteps.updateContract;
import static io.jexxa.jlegmed.examples.ContractSteps.validateContract;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertFalse;

class DSLStepTest {
    private static JLegMed jlegmed;

    @BeforeEach
    void initBeforeEach()
    {
        jlegmed = new JLegMed(TransformDataTest.class)
                .disableStrictFailFast() // For testing purposes, we disable fail fast
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
        var contractStorage = new Stack<ContractFilter.UpdatedContract>();
        jlegmed.newFlowGraph("DSLTest")
                .every(10, MILLISECONDS)

                .receive(ContractFilter.NewContract.class).from(contractGenerator)
                .then().processWith(updateContract)
                .then().processWith(validateContract)
                .then().sinkTo( storeContract( contractStorage) );
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> contractStorage.size() >= 3);
        assertFalse( contractStorage.isEmpty());
    }
}
