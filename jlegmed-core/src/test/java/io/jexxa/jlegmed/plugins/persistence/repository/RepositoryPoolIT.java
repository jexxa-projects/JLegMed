package io.jexxa.jlegmed.plugins.persistence.repository;

import io.jexxa.jlegmed.core.FailFastException;
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
        jLegMed = new JLegMed(RepositoryPoolIT.class)
                .useTechnology(RepositoryPool.class)
                .disableBanner();
    }

    @AfterEach
    void deInit() {
        jLegMed.stop();
    }

    @Test
    void failFastInvalidProperties() {
        //Arrange
        jLegMed.newFlowGraph("HelloWorld")
                .every(10, MILLISECONDS)
                .receive(String.class).from(() -> "Hello World")

                .and().processWith( data -> new TextEntity(data, UUID.randomUUID().toString()) )
                .and().consumeWith( TestRepository::add ).useProperties("invalid-pw-jdbc-connection");

        //Act/Assert
        assertThrows(FailFastException.class, jLegMed::start);
    }
}
