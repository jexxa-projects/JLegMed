package io.jexxa.jlegmed.plugins.http;

import io.javalin.Javalin;
import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.core.VersionInfo;
import io.jexxa.jlegmed.plugins.generic.MessageCollector;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import io.jexxa.jlegmed.plugins.http.producer.HTTPReaderContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.jexxa.jlegmed.plugins.http.producer.HTTPReader.APPLICATION_TYPE;
import static io.jexxa.jlegmed.plugins.http.producer.HTTPReader.CONTENT_TYPE;
import static io.jexxa.jlegmed.plugins.http.producer.HTTPReader.httpURL;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class HTTPReaderTest {
    private static Javalin javalin;
    @Test
    void testFlowGraph() {
        //Arrange
        var messageCollector = new MessageCollector<VersionInfo>();
        var jlegmed = new JLegMed(HTTPReaderTest.class);

        jlegmed.newFlowGraph("HTMLReader")

                .each(50, MILLISECONDS)
                .receive(VersionInfo.class).from(httpURL("http://localhost:7070/"))

                .andProcessWith( GenericProcessors::idProcessor )
                .andProcessWith( GenericProcessors::consoleLogger )
                .andProcessWith( messageCollector::collect);

        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        jlegmed.stop();
    }


    @Test
    void testFunctionalHTTPReader() {
        //Arrange
        var messageCollector = new MessageCollector<VersionInfo>();
        var jlegmed = new JLegMed(HTTPReaderTest.class);

        jlegmed.newFlowGraph("HTMLReader")

                .each(50, MILLISECONDS)
                .receive(VersionInfo.class).from(httpURL(HTTPReaderTest::readHTTPData))

                .andProcessWith( GenericProcessors::idProcessor )
                .andProcessWith( GenericProcessors::consoleLogger )
                .andProcessWith( messageCollector::collect);

        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        jlegmed.stop();
    }

    private static <T> T readHTTPData(HTTPReaderContext<T> readerContext)
    {
        return readerContext.unirest().get("http://localhost:7070/")
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(readerContext.type())
                .getBody();
    }

    @BeforeAll
    static void startWebservice() {
        var versionInfo = new VersionInfo("a","b", "s", "d" );
        javalin = Javalin.create(javalinConfig -> javalinConfig.showJavalinBanner = false)
                .get("/", ctx -> ctx.json(versionInfo))
                .start(7070);
    }


    @AfterAll
    static void stopWebService()
    {
        javalin.stop();
    }

}
