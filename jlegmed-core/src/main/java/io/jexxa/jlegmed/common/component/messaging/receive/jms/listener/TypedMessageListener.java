package io.jexxa.jlegmed.common.component.messaging.receive.jms.listener;

import java.util.Objects;


@SuppressWarnings("unused")
public abstract class TypedMessageListener<T> extends JSONMessageListener
{
    private final Class<T> clazz;

    protected TypedMessageListener(Class<T> clazz)
    {
        this.clazz = Objects.requireNonNull( clazz );
    }

    protected abstract void onMessage(T message);
    @Override
    public final void onMessage(String message)
    {
        onMessage( fromJson(message, clazz ));
    }

}
