package io.jexxa.jlegmed.plugins.messaging.tcp;

import io.jexxa.adapterapi.JexxaContext;
import io.jexxa.jlegmed.core.FailFastException;
import io.jexxa.jlegmed.core.filter.FilterContext;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("java:S6548")
public class TCPConnectionPool {
    private static final TCPConnectionPool INSTANCE = new TCPConnectionPool();

    private final Map<FilterContext, TCPConnection> messageSenderMap = new ConcurrentHashMap<>();

    public static TCPConnection tcpConnection(FilterContext filterContext)
    {
        return INSTANCE.internalTCPConnection(filterContext);
    }


    private void initTCPConnections(Properties properties)
    {
        try {
            if (properties.containsKey(TCPProperties.TCP_ADDRESS)) {
                new TCPConnection(properties);
            }
        } catch(RuntimeException e) {
            throw new FailFastException("Could not init TCP connection for filter properties "
                + ". Reason: " + e.getMessage(), e );
        }
    }

    private TCPConnection internalTCPConnection(FilterContext filterContext)
    {
        INSTANCE.messageSenderMap.computeIfAbsent(filterContext,
                key -> new TCPConnection(filterContext.filterProperties()));

        return INSTANCE.messageSenderMap.get(filterContext);
    }

    private void cleanup()
    {
        messageSenderMap.forEach((key, value) -> value.close());
        messageSenderMap.clear();
    }


    private TCPConnectionPool()
    {
        JexxaContext.registerCleanupHandler(this::cleanup);
        JexxaContext.registerValidationHandler(this::initTCPConnections);
    }
}
