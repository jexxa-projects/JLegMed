package io.jexxa.jlegmed.core;

import static java.util.Objects.requireNonNull;

public record Content(Object data) {
    public Content(Object data) {
        this.data = requireNonNull(data);
    }

    public <T> T getData(Class<T> dataType) {
        return dataType.cast(data);
    }

    @Override
    public String toString() {
        return data.toString();
    }
}
