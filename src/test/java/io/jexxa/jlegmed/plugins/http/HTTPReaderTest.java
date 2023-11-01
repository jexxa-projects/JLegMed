package io.jexxa.jlegmed.plugins.http;

import io.javalin.Javalin;
import io.jexxa.jlegmed.core.VersionInfo;
import io.jexxa.jlegmed.core.filter.producer.Producer;
import io.jexxa.jlegmed.plugins.generic.pipe.CollectingInputPipe;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.jexxa.jlegmed.plugins.http.producer.HTTPReader.http;
import static io.jexxa.jlegmed.plugins.http.producer.HTTPReader.httpURL;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HTTPReaderTest {
    private static Javalin javalin;

    @Test
    void testHttpURL()
    {
        //Arrange
        var expectedResult = new VersionInfo("a","b", "s", "d" );
        var receivingPipe = new CollectingInputPipe<VersionInfo>();

        Producer<VersionInfo> objectUnderTest = httpURL("http://localhost:7070/");
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

        var objectUnderTest = http(versionInfo::read);
        objectUnderTest.producingType(VersionInfo.class);

        objectUnderTest.outputPipe().connectTo(receivingPipe);

        //Act
        objectUnderTest.reachStarted();

        //Assert
        assertEquals(1, receivingPipe.getCollectedData().size());
        assertEquals(expectedResult, receivingPipe.getCollectedData().get(0));

        objectUnderTest.reachDeInit();
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
