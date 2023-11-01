package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.dto.incoming.NewContract;
import io.jexxa.jlegmed.dto.incoming.UpdatedContract;

public class TestFilter {
    public static NewContract newContract(FilterContext context) {
        var stateID = "newContract";
        var currentCounter = context.state(stateID,Integer.class).orElse(1);

        return new NewContract( context.updateState(stateID, currentCounter+1) );
    }

    public static UpdatedContract transformToUpdatedContract(NewContract newContract) {
        return new UpdatedContract(newContract.contractNumber(), "transformToUpdatedContract");
    }

    public static UpdatedContract contextTransformer(NewContract newContract, FilterContext context) {
        return new UpdatedContract(newContract.contractNumber(), "propertiesTransformer" + context.getClass().getSimpleName());
    }

    public static Integer duplicateProducer(FilterContext context) {
        var stateID = "duplicateProducer";
        var currentCounter = context.state(stateID, Integer.class).orElse(0);
        var filterState = context.processingState();

        if (!filterState.isProcessingAgain()) {
            filterState.processAgain();
            return context.updateState(stateID, currentCounter+1);
        }

        return currentCounter;
    }


    public static <T> T skipEachSecondMessage(T data, FilterContext context)
    {
        var stateID = "skipEachSecondMessage";
        int currentCounter = context.state(stateID, Integer.class).orElse(1);
        context.updateState(stateID, currentCounter+1);

        if (currentCounter % 2 == 0) {
            return null;
        }
        return data;
    }

}
