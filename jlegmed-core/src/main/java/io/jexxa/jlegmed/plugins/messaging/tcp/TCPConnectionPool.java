package io.jexxa.jlegmed.plugins.messaging.tcp;

import io.jexxa.jlegmed.core.BootstrapRegistry;
import io.jexxa.jlegmed.core.FailFastException;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.filter.FilterProperties;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("java:S6548")
public class TCPConnectionPool {
    private static final TCPConnectionPool INSTANCE = new TCPConnectionPool();

    private final Map<String, TCPConnection> messageSenderMap = new HashMap<>();

    public static TCPConnection tcpConnection(FilterContext filterContext)
    {
        return INSTANCE.internalTCPConnection(filterContext.filterProperties());
    }


    private void initTCPConnections(FilterProperties filterProperties)
    {
        try {
            if (filterProperties.properties().containsKey(TCPProperties.TCP_ADDRESS)) {
                internalTCPConnection(filterProperties);
            }
        } catch(RuntimeException e) {
            throw new FailFastException("Could not init TCP connection for filter properties " + filterProperties.name()
                + ". Reason: " + e.getMessage(), e );
        }
    }

    private TCPConnection internalTCPConnection(FilterProperties filterProperties)
    {
        INSTANCE.messageSenderMap.computeIfAbsent(filterProperties.name(),
                key -> new TCPConnection(filterProperties));

        return INSTANCE.messageSenderMap.get(filterProperties.name());
    }

    private void cleanup()
    {
        messageSenderMap.forEach((key, value) -> value.close());
        messageSenderMap.clear();
    }


    private TCPConnectionPool()
    {
        BootstrapRegistry.registerBootstrapHandler(this::cleanup);
        BootstrapRegistry.registerFailFastHandler(this::initTCPConnections);
    }
}
