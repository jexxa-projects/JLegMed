package io.jexxa.jlegmed.core;

import io.jexxa.jlegmed.core.filter.FilterProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Deprecated
public final class BootstrapRegistry {

    private static final BootstrapRegistry INSTANCE = new BootstrapRegistry();

    private final List<Consumer<FilterProperties>> failFastHandler = new ArrayList<>();
    private final List<Runnable> bootstrapHandler = new ArrayList<>();

    public static void registerFailFastHandler(Consumer<FilterProperties> propertiesHandler)
    {
        INSTANCE.failFastHandler.add(propertiesHandler);
    }

    public static void registerBootstrapHandler(Runnable bootStrapHandler)
    {
        INSTANCE.bootstrapHandler.add(bootStrapHandler);
    }


    public static void initFailFast(FilterProperties filterProperties)
    {
        INSTANCE.failFastHandler.forEach(element -> element.accept(filterProperties));
    }

    public static void bootstrapServices()
    {
        INSTANCE.bootstrapHandler.forEach(Runnable::run);
    }
}
