package io.jexxa.jlegmed.plugins.persistence.repository;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.pipes.OutputPipe;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Stack;
import java.util.UUID;

import static io.jexxa.common.facade.jdbc.JDBCConnectionPool.getConnection;
import static io.jexxa.jlegmed.plugins.persistence.repository.RepositoryPool.getRepository;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RepositoryIT {

    private static JLegMed jLegMed;
    record TextEntity (String data, String key) { }

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
        RepositoryPool.init();
        var messageCollector = new Stack<TextEntity>();

        jLegMed.bootstrapFlowGraph("reset database")
                .execute((filterContext) -> dropTable(filterContext, TextEntity.class)).useProperties("test-jdbc-connection");


        jLegMed.newFlowGraph("HelloWorld")
                .every(10, MILLISECONDS)
                .receive(String.class).from(() -> "Hello World")

                .and().processWith( data -> new TextEntity(data, UUID.randomUUID().toString()) )
                .and().processWith( RepositoryIT::add ).useProperties("test-jdbc-connection")
                .and().consumeWith( messageCollector::push );
        //Act
        jLegMed.start();

        //Assert
        await().atMost(3, SECONDS).until(() -> messageCollector.size() >= 3);
    }


    @Test
    void readData() {
        //Arrange
        RepositoryPool.init();

        var messageCollector = new Stack<TextEntity>();
        var numberOfData = 10;
        bootstrapTestData(jLegMed, numberOfData);

        jLegMed.newFlowGraph("Read Data")
                .repeat(1)
                .receive(TextEntity.class).from(RepositoryIT::read).useProperties("test-jdbc-connection")
                .and().processWith( messageCollector::push );

        //Act
        jLegMed.start();
        await().atMost(3, SECONDS).until(jLegMed::waitUntilFinished );

        //Assert
        assertEquals(numberOfData, messageCollector.size());
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
                .tableCommand()
                .dropTableIfExists(table)
                .asIgnore();
    }
}
