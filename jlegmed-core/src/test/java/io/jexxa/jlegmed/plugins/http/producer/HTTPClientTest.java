package io.jexxa.jlegmed.plugins.http.producer;

import io.javalin.Javalin;
import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.core.VersionInfo;
import io.jexxa.jlegmed.core.filter.producer.Producer;
import io.jexxa.jlegmed.plugins.generic.pipe.CollectingInputPipe;
import io.jexxa.jlegmed.plugins.generic.processor.GenericCollector;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.jexxa.jlegmed.plugins.http.producer.HTTPClient.httpClient;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HTTPClientTest {
    private static Javalin javalin;

    @Test
    void testHttpURL()
    {
        //Arrange
        var expectedResult = new VersionInfo("a","b", "s", "d" );
        var receivingPipe = new CollectingInputPipe<VersionInfo>();

        Producer<VersionInfo> objectUnderTest = httpClient("http://localhost:7070/");
        objectUnderTest.outputPipe().connectTo(receivingPipe);
        objectUnderTest.producingType(VersionInfo.class);

        //Act
        objectUnderTest.reachStarted();

        //Assert
        assertEquals(1, receivingPipe.getCollectedData().size());
        assertEquals(expectedResult, receivingPipe.getCollectedData().get(0));

        objectUnderTest.reachDeInit();
    }


    @Test
    void testFunctionalHTTPReader() {
        //Arrange
        var expectedResult = new VersionInfo("a","b", "s", "d" );
        var receivingPipe = new CollectingInputPipe<VersionInfo>();
        var versionInfo = new VersionInfoReader("http://localhost:7070/");

        var objectUnderTest = httpClient(versionInfo::read);
        objectUnderTest.producingType(VersionInfo.class);

        objectUnderTest.outputPipe().connectTo(receivingPipe);

        //Act
        objectUnderTest.reachStarted();

        //Assert
        assertEquals(1, receivingPipe.getCollectedData().size());
        assertEquals(expectedResult, receivingPipe.getCollectedData().get(0));

        objectUnderTest.reachDeInit();
    }

    @Test
    void testHTTPClientFlowGraph() {
        //Arrange
        var expectedResult = new VersionInfo("a","b", "s", "d" );
        var messageCollector = new GenericCollector<VersionInfo>();
        JLegMed jLegMed = new JLegMed(HTTPClientTest.class);

        jLegMed.newFlowGraph("HTTPClientFlowGraph")
                .each(10, MILLISECONDS)
                .receive(VersionInfo.class).from(httpClient("http://localhost:7070/"))

                .and().processWith( GenericProcessors::idProcessor )
                .and().consumeWith( messageCollector::collect );
        //Act
        jLegMed.start();
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        jLegMed.stop();

        //Assert
        assertEquals(expectedResult, messageCollector.getMessages().get(0));
        assertEquals(expectedResult, messageCollector.getMessages().get(1));
        assertEquals(expectedResult, messageCollector.getMessages().get(2));
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