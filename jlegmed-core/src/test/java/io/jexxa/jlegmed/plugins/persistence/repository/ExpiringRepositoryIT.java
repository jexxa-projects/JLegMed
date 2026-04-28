package io.jexxa.jlegmed.plugins.persistence.repository;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.filter.FilterProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Stream;

import static io.jexxa.jlegmed.plugins.persistence.repository.RepositoryConfig.REPOSITORY_CONFIG;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ExpiringRepositoryIT {

    private static JLegMed jLegMed;

    @BeforeEach
    void init() {
        jLegMed = new JLegMed(RepositoryIT.class)
                .useTechnology(RepositoryPool.class)
                .disableBanner();
    }

    @AfterEach
    void deInit() {
        if (jLegMed != null)
        {
            jLegMed.stop();
        }
    }

    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void testExpireIn(Properties properties) {
        //Arrange
        List<TextEntity> entities = Stream.generate(() -> new TextEntity("data", UUID.randomUUID().toString()))
                .limit(10)
                .toList();

        var filterContext = new FilterContext();
        filterContext.filterProperties(new FilterProperties("test", properties));

        var objectUnderTest =  RepositoryPool.getExpiringRepository(
                TextEntity.class, String.class,
                TextEntity::key, filterContext);
        objectUnderTest.removeAll();
        entities.forEach(objectUnderTest::add);


        //Act
        objectUnderTest.get().forEach( data -> objectUnderTest.expireIn(data.key(), Duration.ofSeconds(2)));

        //Assert
        assertEquals(10, objectUnderTest.get().size());

        await().atMost(3, SECONDS).until(() -> {objectUnderTest.purgeData(); return objectUnderTest.get().isEmpty();});
    }


    @Test
    void expireAt() {
    }
}