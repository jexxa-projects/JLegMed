package io.jexxa.jlegmed.plugins.http.producer;

import io.jexxa.jlegmed.core.VersionInfo;

import static io.jexxa.jlegmed.plugins.http.producer.HTTPClient.APPLICATION_TYPE;
import static io.jexxa.jlegmed.plugins.http.producer.HTTPClient.CONTENT_TYPE;

public class VersionInfoReader {
    private final String url;
    public VersionInfoReader(String url) {
        this.url = url;
    }

    public void read(HTTPClientContext<VersionInfo> readerContext)
    {
        var result = readerContext.unirest().get(url)
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(readerContext.dataType())
                .getBody();

        readerContext.outputPipe().accept(result);
    }
}
