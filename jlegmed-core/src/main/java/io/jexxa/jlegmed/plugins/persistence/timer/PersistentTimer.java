package io.jexxa.jlegmed.plugins.persistence.timer;

import io.jexxa.jlegmed.core.filter.FilterContext;

import java.time.Instant;

import static io.jexxa.jlegmed.plugins.persistence.repository.RepositoryPool.getRepository;
import static io.jexxa.jlegmed.plugins.persistence.timer.TimerConfig.timerConfigOf;

public class PersistentTimer {
    public static final String START_TIME = "start.time";
    public static TimeInterval nextInterval(TimerID timerID, FilterContext filterContext) {
        if (!filterContext.properties().containsKey(START_TIME))
        {
            throw new IllegalArgumentException(START_TIME + " is missing in properties " + filterContext.propertiesName());
        }

        return nextInterval(timerConfigOf(timerID,
                Instant.parse(filterContext.properties().getProperty(START_TIME) )), filterContext);
    }

    public static TimeInterval nextInterval(TimerConfig timerConfig, FilterContext filterContext)
    {
        var repository = getRepository(TimerState.class,
                TimerState::timerID,
                filterContext);

        //1. Calculate a new TimerState.
        TimerState timerState = repository
                .get(timerConfig.timerID())
                .map(state -> new TimerState(
                        state.timerID(),
                        new TimeInterval(state.timerInterval.end())))
                .orElseGet(() -> new TimerState(timerConfig.timerID(), new TimeInterval(timerConfig.startTime(), Instant.now())));

        //2. Store the new TimeInterval in Repository
        if (repository.get(timerConfig.timerID()).isPresent())
        {
            repository.update(timerState);
        } else {
            repository.add(timerState);
        }

        //3. Return the new interval
        return timerState.timerInterval();
    }


    private record TimerState(TimerID timerID, TimeInterval timerInterval){}
}
