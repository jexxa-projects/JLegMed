package io.jexxa.jlegmed.examples;

import io.jexxa.adapterapi.invocation.function.SerializableFunction;
import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static io.jexxa.jlegmed.core.flowgraph.builder.Step.step;
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
        SerializableFunction<FilterContext, TestFilter.NewContract> createNewContract = TestFilter::newContract;
        var updateContract = step(TestFilter::transformToUpdatedContract);

        var messageCollector = new Stack<TestFilter.UpdatedContract>();
        jlegmed.newFlowGraph("ChangeDataType")
                .every(10, MILLISECONDS)

                .receive(TestFilter.NewContract.class).from(createNewContract)

                .then().processWith(updateContract)
                .then().processWith(GenericProcessors::idProcessor)
                .then().sinkTo( messageCollector::push );
        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.size() >= 3);
        assertFalse( messageCollector.isEmpty());
    }
}
