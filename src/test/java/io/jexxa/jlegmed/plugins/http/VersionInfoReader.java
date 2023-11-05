package io.jexxa.jlegmed.plugins.http;

import io.jexxa.jlegmed.core.VersionInfo;
import io.jexxa.jlegmed.plugins.http.producer.HTTPClientContext;

import static io.jexxa.jlegmed.plugins.http.producer.HTTPClient.APPLICATION_TYPE;
import static io.jexxa.jlegmed.plugins.http.producer.HTTPClient.CONTENT_TYPE;

public class VersionInfoReader {
    private final String url;
    public VersionInfoReader(String url) {
        this.url = url;
    }

    public void read(HTTPClientContext<VersionInfo> readerContext)
    {
        System.out.println("READ " + url);

        var result = readerContext.unirest().get(url)
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(readerContext.dataType())
                .getBody();

        System.out.println(result);

        readerContext.outputPipe().accept(result);
    }
}
