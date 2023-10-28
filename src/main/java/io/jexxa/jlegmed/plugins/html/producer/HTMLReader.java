package io.jexxa.jlegmed.plugins.html.producer;

import io.jexxa.jlegmed.common.wrapper.utils.properties.Secret;
import io.jexxa.jlegmed.core.filter.producer.FunctionalProducer;
import kong.unirest.Unirest;
import kong.unirest.UnirestInstance;

import java.util.Properties;

import static io.jexxa.jlegmed.plugins.html.HTMLProperties.HTTP_PROXY_FILE_PASSWORD;
import static io.jexxa.jlegmed.plugins.html.HTMLProperties.HTTP_PROXY_FILE_USERNAME;
import static io.jexxa.jlegmed.plugins.html.HTMLProperties.HTTP_PROXY_HOST;
import static io.jexxa.jlegmed.plugins.html.HTMLProperties.HTTP_PROXY_PASSWORD;
import static io.jexxa.jlegmed.plugins.html.HTMLProperties.HTTP_PROXY_PORT;
import static io.jexxa.jlegmed.plugins.html.HTMLProperties.HTTP_PROXY_USERNAME;

public class HTMLReader<T> extends FunctionalProducer<T> {
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_TYPE = "application/json";
    private final String url;
    private UnirestInstance unirestInstance;

    public HTMLReader(String url) {
        this.url = url;
    }


    @Override
    public void init() {
        super.init();
        unirestInstance = Unirest.spawnInstance();
        getFilterProperties().ifPresent(this::initUnirest);
    }

    @Override
    protected T doProduce() {
        return unirestInstance.get(url)
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(getType()).getBody();
    }

    @Override
    public void deInit()
    {
        super.deInit();
        unirestInstance.shutDown();
        unirestInstance = null;
    }

    private void initUnirest(Properties properties)
    {
        var host = properties.getProperty(HTTP_PROXY_HOST, "");
        var port = Integer.parseInt(properties.getProperty(HTTP_PROXY_PORT, "0"));

        Secret username = new Secret(properties, HTTP_PROXY_USERNAME, HTTP_PROXY_FILE_USERNAME);
        Secret password = new Secret(properties, HTTP_PROXY_PASSWORD, HTTP_PROXY_FILE_PASSWORD);

        unirestInstance.config().proxy(host, port, username.getSecret(), password.getSecret());
    }

    public static <T> HTMLReader<T> httpURL(String url) {
        return new HTMLReader<>(url);
    }
}
