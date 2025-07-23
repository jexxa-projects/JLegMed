package io.jexxa.jlegmed.plugins.persistence.timer;

import io.jexxa.common.facade.utils.properties.PropertiesUtils;
import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.core.filter.FilterProperties;
import io.jexxa.jlegmed.plugins.persistence.repository.RepositoryPool;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static io.jexxa.jlegmed.core.filter.processor.Processor.processor;
import static io.jexxa.jlegmed.plugins.persistence.timer.TimerConfig.timerConfigOf;
import static io.jexxa.jlegmed.plugins.persistence.timer.TimerID.timerIdOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class PersistentTimerIT {

    @Test
    void nextInterval() {

        //Arrange
        var jLegMed = new JLegMed(PersistentTimerIT.class)
                .useTechnology(RepositoryPool.class);

        List<TimeInterval> result = new ArrayList<>();
        var timerConfig= timerConfigOf(timerIdOf(PersistentTimerIT.class.getSimpleName()));

        var filter = processor(PersistentTimer::nextInterval);
        filter.outputPipe().connectTo(result::add);
        filter.useProperties(FilterProperties.filterPropertiesOf("PersistentTimer", PropertiesUtils.getSubset(jLegMed.getProperties(), "test-jdbc-connection")));
        filter.reachStarted();

        //Act
        filter.process(timerConfig);
        filter.process(timerConfig);

        //Assert
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
    }
}