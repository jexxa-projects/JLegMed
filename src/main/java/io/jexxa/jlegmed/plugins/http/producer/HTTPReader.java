package io.jexxa.jlegmed.plugins.http.producer;

import io.jexxa.jlegmed.common.wrapper.utils.properties.Secret;
import io.jexxa.jlegmed.core.filter.producer.Producer;
import io.jexxa.jlegmed.plugins.http.HTTPProperties;
import kong.unirest.Unirest;
import kong.unirest.UnirestInstance;

import java.util.Properties;
import java.util.function.Consumer;

public abstract class HTTPReader<T> extends Producer<T> {
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_TYPE = "application/json";
    private UnirestInstance unirestInstance;


    @Override
    public void init() {
        super.init();
        unirestInstance = Unirest.spawnInstance();
        properties().ifPresent(element -> initUnirest(element.properties()));
    }


    @Override
    public void deInit()
    {
        super.deInit();
        unirestInstance.shutDown();
        unirestInstance = null;
    }

    @Override
    public void start() {
        super.start();
        produceData();
    }

    protected abstract void produceData();

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
            protected void produceData() {
                var result = getUnirest().get(url)
                        .header(CONTENT_TYPE, APPLICATION_TYPE)
                        .asObject(producingType()).getBody();
                outputPipe().forward(result);
            }
        };
    }

    public static <T> HTTPReader<T> http(Consumer<HTTPReaderContext<T>> function)
    {
        return new HTTPReader<>() {
            @Override
            protected void produceData() {
                function.accept(new HTTPReaderContext<>(getUnirest(), filterContext(), producingType(), outputPipe()::forward));
            }
        };
    }


}
