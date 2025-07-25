package io.jexxa.jlegmed.plugins.http.producer;

import io.jexxa.common.facade.utils.properties.Secret;
import io.jexxa.jlegmed.core.filter.producer.PassiveProducer;
import io.jexxa.jlegmed.plugins.http.HTTPProperties;
import kong.unirest.Unirest;
import kong.unirest.UnirestInstance;

import java.util.Properties;
import java.util.function.Consumer;

import static io.jexxa.jlegmed.plugins.http.HTTPProperties.HTTP_URL;

public abstract class HTTPClient<T> extends PassiveProducer<T> {
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_TYPE = "application/json";
    private UnirestInstance unirestInstance;
    private String url;

    protected HTTPClient() {
        super(HTTPClient.class);
    }

    @Override
    public String name() {
        return HTTPClient.class.getSimpleName() + ":" + url;
    }

    @Override
    public void init() {
        super.init();
        if(producingType() == null)
        {
            throw new IllegalArgumentException("Producing dataType is not configured for " + HTTPClient.class.getSimpleName() + "! -> Configure filter with Producer::producingType<Class<T>>!");
        }
        unirestInstance = Unirest.spawnInstance();
        initUnirest(properties());
        initFilter(properties());
    }


    @Override
    public void deInit()
    {
        super.deInit();
        unirestInstance.shutDown();
        unirestInstance = null;
    }

    protected UnirestInstance getUnirest()
    {
        return unirestInstance;
    }

    private void initFilter(Properties properties)
    {
        if (properties.containsKey(HTTP_URL))
        {
            url = properties.getProperty(HTTP_URL);
        }
    }
    private void initUnirest(Properties properties)
    {
        var host = properties.getProperty(HTTPProperties.HTTP_PROXY_HOST, "");
        var port = 0;
        try {
            port = Integer.parseInt(properties.getProperty(HTTPProperties.HTTP_PROXY_PORT, "0"));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid port specified. '" + properties.getProperty(HTTPProperties.HTTP_PROXY_PORT) +"' can not be converted to int", e);
        }


        Secret username = new Secret(properties, HTTPProperties.HTTP_PROXY_USERNAME, HTTPProperties.HTTP_PROXY_FILE_USERNAME);
        Secret password = new Secret(properties, HTTPProperties.HTTP_PROXY_PASSWORD, HTTPProperties.HTTP_PROXY_FILE_PASSWORD);

        if (!host.isEmpty()) {
            unirestInstance.config().proxy(host, port, username.getSecret(), password.getSecret());
        }
    }

    protected String url()
    {
        return url;
    }

    public static <T> HTTPClient<T> httpClient(String url) {
        return new HTTPClient<>(){
            @Override
            public void produceData() {
                var result = getUnirest().get(url)
                        .header(CONTENT_TYPE, APPLICATION_TYPE)
                        .asObject(producingType()).getBody();
                outputPipe().forward(result);
            }
        };
    }

    public static <T> HTTPClient<T> httpClient() {
        return new HTTPClient<>(){
            @Override
            public void produceData() {
                var result = getUnirest().get(url())
                        .header(CONTENT_TYPE, APPLICATION_TYPE)
                        .asObject(producingType()).getBody();
                outputPipe().forward(result);
            }
        };
    }

    public static <T> HTTPClient<T> httpClient(Consumer<HTTPClientContext<T>> function)
    {
        return new HTTPClient<>() {
            @Override
            public void produceData() {
                function.accept(new HTTPClientContext<>(getUnirest(), filterContext(), producingType(), outputPipe()));
            }
        };
    }
}
