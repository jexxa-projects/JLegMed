package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.jlegmed.core.processor.ProcessorConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class Context {
    private final HashMap<String, Object> contextData = new HashMap<>();
    private final Properties properties;
    private ProcessorConfig processorConfig;

    public Context(Properties properties)
    {
        this.properties = properties;
    }

    public <T> Optional<T> get(String id, Class<T> clazz)
    {
        return Optional.ofNullable(clazz.cast(contextData.get(id)));
    }

    @SuppressWarnings("unused")
    public Properties getProperties()
    {
        return properties;
    }

    public Properties getProperties(String propertiesPrefix)
    {
        var subset = new Properties();

        List<String> result = properties.keySet().stream()
                .map(Object::toString)
                .filter(string -> string.contains(propertiesPrefix))
                .toList();

        result.forEach( element -> subset.put(
                element.substring(element.lastIndexOf(propertiesPrefix) + propertiesPrefix.length() + 1),
                properties.getProperty(element) ));

        return subset;
    }

    public <T> T getProcessorConfig(Class<T> conigType)
    {
        return processorConfig.getConfig(conigType);
    }

    public void setProcessorConfig(ProcessorConfig processorConfig)
    {
        this.processorConfig = processorConfig;
    }

    public <T> T update(String id, T data) {
        contextData.put(id, data);
        return data;
    }

    public static String contextID(Class<?> type, String id)
    {
        return type.getSimpleName() + id;
    }

    public boolean isProcessedAgain() {
        return processorConfig.isProcessedAgain();
    }

    public void processAgain()
    {
        processorConfig.processAgain();
    }
}
