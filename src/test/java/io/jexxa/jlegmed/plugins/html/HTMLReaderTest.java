package io.jexxa.jlegmed.plugins.html;

import io.javalin.Javalin;
import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.core.VersionInfo;
import io.jexxa.jlegmed.plugins.generic.MessageCollector;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.jexxa.jlegmed.plugins.html.producer.HTMLReader.httpURL;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class HTMLReaderTest {
    private static Javalin javalin;
    @Test
    void testFlowGraph() {
        //Arrange
        var messageCollector = new MessageCollector<VersionInfo>();
        var jlegmed = new JLegMed(HTMLReaderTest.class);
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
