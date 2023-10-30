package io.jexxa.jlegmed.plugins.http;

import io.javalin.Javalin;
import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.core.VersionInfo;
import io.jexxa.jlegmed.plugins.generic.MessageCollector;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.jexxa.jlegmed.plugins.http.producer.HTTPReader.http;
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
        var jlegmed = new JLegMed(HTTPReaderTest.class).disableBanner();

        jlegmed.newFlowGraph("HTMLReader")

                .each(50, MILLISECONDS)
                .receive(VersionInfo.class).from(httpURL("http://localhost:7070/"))

                .and().processWith( GenericProcessors::idProcessor )
                .and().processWith( GenericProcessors::consoleLogger )
                .and().processWith( messageCollector::collect);

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
        var versionInfo = new VersionInfoReader("http://localhost:7070/");
        var jlegmed = new JLegMed(HTTPReaderTest.class).disableBanner();

        jlegmed.newFlowGraph("HTMLReader")

                .each(50, MILLISECONDS)
                .receive(VersionInfo.class).from(http(versionInfo::read))

                .and().processWith( GenericProcessors::idProcessor )
                .and().processWith( messageCollector::collect );

        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        jlegmed.stop();
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
