package io.jexxa.jlegmed;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

public class Message {
    private final Object data;

    public Message(Object data)
    {
        this.data = requireNonNull(data);
    }

    public Object getData()
    {
        return data;
    }

    public <T> T getData(Class<T> dataType)
    {
        return dataType.cast(data);
    }

    @Override
    public String toString() { return data.toString(); }
}
