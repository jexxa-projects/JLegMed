package io.jexxa.jlegmed.plugins.persistence.timer;

import io.jexxa.jlegmed.core.JLegMed;
import io.jexxa.jlegmed.core.filter.FilterProperties;
import io.jexxa.jlegmed.core.filter.ProcessingException;
import io.jexxa.jlegmed.plugins.persistence.repository.RepositoryPool;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import static io.jexxa.common.facade.utils.properties.PropertiesUtils.getSubset;
import static io.jexxa.jlegmed.core.filter.processor.Processor.processor;
import static io.jexxa.jlegmed.plugins.persistence.timer.TimerID.timerIdOf;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersistentTimerIT {

    @Test
    void nextInterval() {

        //Arrange
        var jLegMed = new JLegMed(PersistentTimerIT.class)
                .useTechnology(RepositoryPool.class);

        List<TimeInterval> result = new ArrayList<>();
        var timerConfig= timerIdOf(PersistentTimerIT.class.getSimpleName());

        var filter = processor(PersistentTimer::nextInterval);
        filter.outputPipe().connectTo(result::add);
        filter.useProperties(FilterProperties.filterPropertiesOf(
                filter.defaultPropertiesName(),
                getSubset(jLegMed.getProperties(), filter.defaultPropertiesName())));

        filter.reachStarted();

        //Act
        filter.process(timerConfig);
        filter.process(timerConfig);

        //Assert
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
    }


    @Test
    void nextIntervalFails() {

        //Arrange
        var jLegMed = new JLegMed(PersistentTimerIT.class)
                .useTechnology(RepositoryPool.class);

        List<TimeInterval> result = new ArrayList<>();
        var timerConfig= timerIdOf(PersistentTimerIT.class.getSimpleName());

        var filter = processor(PersistentTimer::nextInterval);
        filter.outputPipe().connectTo( data -> validateStartTime(data, result));
        filter.useProperties(FilterProperties.filterPropertiesOf(
                filter.defaultPropertiesName(),
                getSubset(jLegMed.getProperties(), filter.defaultPropertiesName())));

        filter.reachStarted();

        //Act
        assertThrows(ProcessingException.class, () -> filter.process(timerConfig));
        filter.process(timerConfig);

        //Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void flowgraphTest() {

        //Arrange
        var jLegMed = new JLegMed(PersistentTimerIT.class)
                .useTechnology(RepositoryPool.class);
        var result = new Stack<TimeInterval>();

        jLegMed.newFlowGraph("FertigungsplanErstellt")
                .every(10, TimeUnit.MILLISECONDS)

                .receive(TimerID.class).from(() -> timerIdOf("FertigungsplanErstellt"))
                .and().processWith(PersistentTimer::nextInterval)
                .and().processWith(result::push);
        //Act
        jLegMed.start();

        //Assert
        await().atMost(3, TimeUnit.SECONDS).until( () -> result.size() > 2);
        jLegMed.stop();
    }


    static TimeInterval storedTimeInterval = null;

    static void validateStartTime(TimeInterval timeInterval, List<TimeInterval> result)
    {
        if (storedTimeInterval == null)
        {
            storedTimeInterval = timeInterval;
            throw new RuntimeException("Simulate Error: and wait for the next TimeInterval");
        }

        if (storedTimeInterval.begin().equals(timeInterval.begin())
                && !storedTimeInterval.end().equals(timeInterval.end())
        )
        {
            result.add(timeInterval);
        } else {
            throw new RuntimeException("Simulate Error: next TimeInterval is equal to first one");
        }
    }

}