package io.jexxa.jlegmed.examples.contract;

public record NewContract(int contractNumber) {
    public static NewContract newContract(int contractNumber) {
        return new NewContract(contractNumber);
    }
}
