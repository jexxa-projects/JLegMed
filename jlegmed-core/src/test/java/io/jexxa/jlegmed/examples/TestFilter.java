package io.jexxa.jlegmed.examples;

import io.jexxa.jlegmed.core.filter.FilterContext;

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

    public record NewContract(int contractNumber)
    {
        public static NewContract newContract(int contractNumber)
        {
            return new NewContract(contractNumber);
        }
    }

    public record UpdatedContract(int contract, String updateInformation) {
    }
}
