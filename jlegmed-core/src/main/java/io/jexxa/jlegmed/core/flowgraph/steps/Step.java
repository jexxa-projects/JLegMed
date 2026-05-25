package io.jexxa.jlegmed.core.flowgraph.steps;

public class Step<S extends Step<S>> {
    private String properties = "";

    @SuppressWarnings("unchecked")
    protected S self() {
        return (S) this;
    }

    public S useProperties(String properties) {
        this.properties = properties;
        return self();
    }

    public String properties() {
        return properties;
    }
}
