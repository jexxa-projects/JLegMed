package io.jexxa.jlegmed.plugins.http.producer;

import io.jexxa.jlegmed.common.wrapper.utils.properties.Secret;
import io.jexxa.jlegmed.core.filter.producer.FunctionalProducer;
import io.jexxa.jlegmed.plugins.http.HTTPProperties;
import kong.unirest.Unirest;
import kong.unirest.UnirestInstance;

import java.util.Properties;
import java.util.function.Function;

public abstract class HTTPReader<T> extends FunctionalProducer<T> {
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_TYPE = "application/json";
    private UnirestInstance unirestInstance;


    @Override
    public void init() {
        super.init();
        unirestInstance = Unirest.spawnInstance();
        getProperties().ifPresent( element -> initUnirest(element.properties()));
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
    private void initUnirest(Properties properties)
    {
        var host = properties.getProperty(HTTPProperties.HTTP_PROXY_HOST, "");
        var port = Integer.parseInt(properties.getProperty(HTTPProperties.HTTP_PROXY_PORT, "0"));

        Secret username = new Secret(properties, HTTPProperties.HTTP_PROXY_USERNAME, HTTPProperties.HTTP_PROXY_FILE_USERNAME);
        Secret password = new Secret(properties, HTTPProperties.HTTP_PROXY_PASSWORD, HTTPProperties.HTTP_PROXY_FILE_PASSWORD);

        unirestInstance.config().proxy(host, port, username.getSecret(), password.getSecret());
    }

    public static <T> HTTPReader<T> httpURL(String url) {
        return new HTTPReader<>(){
            @Override
            protected T doProduce() {
                return getUnirest().get(url)
                        .header(CONTENT_TYPE, APPLICATION_TYPE)
                        .asObject(getType()).getBody();
            }
        };
    }

    public static <T> HTTPReader<T> httpURL(Function<HTTPReaderContext<T>, T> function)
    {
        return new HTTPReader<>() {
            @Override
            protected T doProduce() {
                var properties = new Properties();
                if (getProperties().isPresent())
                {
                    properties = getProperties().orElseThrow().properties();
                }
                return function.apply(new HTTPReaderContext<>(getUnirest(), properties, getType()));
            }
        };
    }


}
