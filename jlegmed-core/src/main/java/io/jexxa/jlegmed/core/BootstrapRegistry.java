package io.jexxa.jlegmed.core;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import io.jexxa.common.facade.logger.SLF4jLogger;
import io.jexxa.jlegmed.core.filter.FilterProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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


    private ScanResult getScanResult()
    {
        return new ClassGraph()
                        .enableAnnotationInfo()
                        .enableClassInfo()
                        .acceptPackages("io.jexxa.jlegmed.plugins")
                        .scan();
    }

    public static void loadMessagePools()
    {
        INSTANCE.internalLoadMessagePools();
    }

    private void internalLoadMessagePools()
    {
        try(var scanResults = getScanResult())
        {
            var classesToLoad = scanResults
                    .getAllClasses().filter(element -> element.getName().contains("Pool"))
                    .stream().toList();

            classesToLoad.forEach( element -> {
                try {
                    Class.forName(element.getName());
                } catch (ClassNotFoundException e) {
                    SLF4jLogger.getLogger(BootstrapRegistry.class).warn("Could not init Pool {} ", element.getName());
                }
            });
        }

    }
}
