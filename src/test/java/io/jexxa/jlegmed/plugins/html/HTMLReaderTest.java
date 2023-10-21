package io.jexxa.jlegmed.plugins.html;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.core.VersionInfo;
import io.jexxa.jlegmed.core.flowgraph.AbstractFlowGraph;
import io.jexxa.jlegmed.core.flowgraph.FlowGraph;
import io.jexxa.jlegmed.core.producer.Producer;
import io.jexxa.jlegmed.core.producer.ProducerURL;
import io.jexxa.jlegmed.plugins.generic.MessageCollector;
import io.jexxa.jlegmed.plugins.generic.processor.GenericProcessors;
import io.jexxa.jlegmed.plugins.html.producer.HTMLProducer;
import org.junit.jupiter.api.Test;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class HTMLReaderTest {
    @Test
    void testFlowGraph() {
        //Arrange
        var messageCollector = new MessageCollector<VersionInfo>();
        var jlegmed = new JLegMed(HTMLReaderTest.class);
        jlegmed.newFlowGraph("HTMLReader")
                .each(50, MILLISECONDS)
                //.receive(VersionInfo.class).from(httpURL("http://localhost:7503/BoundedContext/contextVersion")).asJson()
                .receive(VersionInfo.class).generatedWith(() -> new VersionInfo("a","b", "s", "d" ))
                .andProcessWith( GenericProcessors::idProcessor )
                .andProcessWith( GenericProcessors::consoleLogger )
                .andProcessWith( messageCollector::collect);

        //Act
        jlegmed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
        jlegmed.stop();
    }

    public static class HTMLReaderURL implements ProducerURL {

        private final String url;
        private FlowGraph flowGraph;

        public HTMLReaderURL(String url)
        {
            this.url = url;
        }
        @Override
        public <T> Producer init(AbstractFlowGraph<T> flowGraph) {
            this.flowGraph = flowGraph;
            return new HTMLProducer(url);
        }

        public FlowGraph asJson()
        {
            return flowGraph;
        }
        public static HTMLReaderURL httpURL(String url)
        {
            return new HTMLReaderURL(url);
        }
    }
}
