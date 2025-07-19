package io.jexxa.jlegmed.plugins.http.producer;

import io.javalin.Javalin;
import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.core.VersionInfo;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Stack;

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
        var result = new ArrayList<VersionInfo>();

        HTTPClient<VersionInfo> objectUnderTest = httpClient("http://localhost:7070/");
        objectUnderTest.outputPipe().connectTo(result::add);
        objectUnderTest.producingType(VersionInfo.class);
        objectUnderTest.reachStarted();

        //Act
        objectUnderTest.produceData();

        //Assert
        assertEquals(1, result.size());
        assertEquals(expectedResult, result.get(0));

        objectUnderTest.reachDeInit();
    }


    @Test
    void testFunctionalHTTPReader() {
        //Arrange
        var expectedResult = new VersionInfo("a","b", "s", "d" );
        var result = new ArrayList<VersionInfo>();
        var versionInfo = new VersionInfoReader("http://localhost:7070/");

        var objectUnderTest = httpClient(versionInfo::read);
        objectUnderTest.producingType(VersionInfo.class);

        objectUnderTest.outputPipe().connectTo(result::add);
        objectUnderTest.reachStarted();

        //Act
        objectUnderTest.produceData();

        //Assert
        assertEquals(1, result.size());
        assertEquals(expectedResult, result.get(0));

        objectUnderTest.reachDeInit();
    }

    @Test
    void testHTTPClientFlowGraph() {
        //Arrange
        var expectedResult = new VersionInfo("a","b", "s", "d" );
        var messageCollector = new Stack<VersionInfo>();
        JLegMed jLegMed = new JLegMed(HTTPClientTest.class);

        jLegMed.newFlowGraph("HTTPClientFlowGraph")
                .every(10, MILLISECONDS)
                .receive(VersionInfo.class).from(httpClient()).useProperties("test-http-connection")

                .and().processWith( GenericProcessors::idProcessor )
                .and().consumeWith( messageCollector::push );
        //Act
        jLegMed.start();
        await().atMost(3, SECONDS).until(() -> messageCollector.size() >= 3);
        jLegMed.stop();

        //Assert
        assertEquals(expectedResult, messageCollector.toArray()[0]);
        assertEquals(expectedResult, messageCollector.toArray()[1]);
        assertEquals(expectedResult, messageCollector.toArray()[2]);
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
