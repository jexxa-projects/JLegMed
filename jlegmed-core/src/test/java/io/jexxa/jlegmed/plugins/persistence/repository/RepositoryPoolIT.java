package io.jexxa.jlegmed.plugins.persistence.repository;

import io.jexxa.jlegmed.core.JLegMed;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RepositoryPoolIT {
    private static JLegMed jLegMed;
    @BeforeEach
    void init() {
        jLegMed = new JLegMed(RepositoryPoolIT.class).disableBanner();
    }

    @AfterEach
    void deInit() {
        jLegMed.stop();
    }

    @Test
    void failFastInvalidProperties() {
        //Arrange
        RepositoryPool.init();

        jLegMed.newFlowGraph("HelloWorld")
                .every(10, MILLISECONDS)
                .receive(String.class).from(() -> "Hello World")

                .and().processWith( data -> new RepositoryIT.TextEntity(data, UUID.randomUUID().toString()) )
                .and().consumeWith( RepositoryIT::add ).useProperties("invalid-pw-jdbc-connection");

        //Act/Assert
        assertThrows(IllegalArgumentException.class, () -> jLegMed.start());
    }
}
