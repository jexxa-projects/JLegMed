package io.jexxa.jlegmed.plugins.persistence.repository;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.pipes.OutputPipe;
import io.jexxa.jlegmed.plugins.generic.processor.GenericCollector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.jexxa.common.facade.jdbc.JDBCConnectionPool.getConnection;
import static io.jexxa.jlegmed.plugins.persistence.repository.RepositoryPool.getRepository;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RepositoryIT {

    private static JLegMed jLegMed;

    @BeforeEach
    void init() {
        jLegMed = new JLegMed(RepositoryIT.class).disableBanner();
    }

    @AfterEach
    void deInit() {
        if (jLegMed != null)
        {
            jLegMed.stop();
        }
    }

    @Test
    void testFlowGraph() {
        //Arrange
        var messageCollector = new GenericCollector<TextEntity>();

        jLegMed.bootstrapFlowGraph("reset database")
                .execute((filterContext) -> dropTable(filterContext, TextEntity.class)).useProperties("test-jdbc-connection");


        jLegMed.newFlowGraph("HelloWorld")
                .every(10, MILLISECONDS)
                .receive(String.class).from(() -> "Hello World")

                .and().processWith( data -> new TextEntity(data, UUID.randomUUID().toString()) )
                .and().processWith( RepositoryIT::add ).useProperties("test-jdbc-connection")
                .and().consumeWith( messageCollector::collect );
        //Act
        jLegMed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.getNumberOfReceivedMessages() >= 3);
    }


    @Test
    void readData() {
        //Arrange
        var messageCollector = new GenericCollector<TextEntity>();
        var numberOfData = 10;
        bootstrapTestData(jLegMed, numberOfData);

        jLegMed.newFlowGraph("Read Data")
                .repeat(1)
                .receive(TextEntity.class).from(RepositoryIT::read).useProperties("test-jdbc-connection")
                .and().processWith( messageCollector::collect );

        //Act
        jLegMed.start();
        await().atMost(3, SECONDS).until(jLegMed::waitUntilFinished );

        //Assert
        assertEquals(numberOfData, messageCollector.getNumberOfReceivedMessages());
    }

    private void bootstrapTestData(JLegMed jLegMed, int numberOfData) {
        jLegMed.bootstrapFlowGraph("reset database")
                .execute((filterContext) -> dropTable(filterContext, TextEntity.class)).useProperties("test-jdbc-connection");

        jLegMed.bootstrapFlowGraph("Init test data")
                .repeat(numberOfData)
                .receive(String.class).from(() -> "Hello World")

                .and().processWith( data -> new TextEntity(data, UUID.randomUUID().toString()) )
                .and().consumeWith( RepositoryIT::add).useProperties("test-jdbc-connection");
    }



    record TextEntity (String data, String key) { }
    public static TextEntity add(TextEntity textEntity, FilterContext filterContext)
    {
        return getRepository(TextEntity.class, TextEntity::key, filterContext).add(textEntity);
    }

    public static void read(FilterContext filterContext, OutputPipe<TextEntity> outputPipe)
    {
        getRepository(TextEntity.class, TextEntity::key, filterContext).get().forEach(outputPipe::forward);
    }

    public static  <T> void dropTable(FilterContext filterContext, Class<T> table){
        getConnection(filterContext.properties(), filterContext)
                .createTableCommand()
                .dropTableIfExists(table)
                .asIgnore();
    }
}
