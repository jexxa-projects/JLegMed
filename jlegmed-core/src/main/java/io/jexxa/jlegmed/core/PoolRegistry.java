package io.jexxa.jlegmed.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

public final class PoolRegistry {

    private static final PoolRegistry INSTANCE = new PoolRegistry();

    private final List<Consumer<Properties>> initHandler = new ArrayList<>();

    public static void registerInitHandler(Consumer<Properties> initHandler)
    {
        INSTANCE.initHandler.add(initHandler);
    }

    static void init(Properties properties)
    {
        INSTANCE.initHandler.forEach( element -> element.accept(properties));
    }


}