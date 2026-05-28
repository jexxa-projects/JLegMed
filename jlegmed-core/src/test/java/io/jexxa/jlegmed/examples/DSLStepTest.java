package io.jexxa.jlegmed.examples;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.examples.contract.NewContract;
import io.jexxa.jlegmed.examples.contract.UpdatedContract;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static io.jexxa.jlegmed.examples.plugins.ContractSteps.storeContract;
import static io.jexxa.jlegmed.examples.plugins.ContractSteps.contractGenerator;
import static io.jexxa.jlegmed.examples.plugins.ContractSteps.updateContract;
import static io.jexxa.jlegmed.examples.plugins.ContractSteps.validateContract;
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
        var contractStorage = new Stack<UpdatedContract>();
        jlegmed.newFlowGraph("DSLTest")
                .every(10, MILLISECONDS)

                .receive(NewContract.class).from(contractGenerator)
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
