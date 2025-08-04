package io.jexxa.jlegmed.plugins.persistence.timer;

import io.jexxa.common.facade.logger.SLF4jLogger;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.filter.ProcessingException;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

import java.time.Instant;

import static io.jexxa.jlegmed.plugins.persistence.repository.RepositoryPool.getRepository;
import static io.jexxa.jlegmed.plugins.persistence.timer.TimerConfig.timerConfigOf;

public class PersistentTimer {
    public static final String START_TIME = "start.time";
    public static void nextInterval(TimerID timerID, FilterContext filterContext, OutputPipe<TimeInterval> outputPipe) {
        if (!filterContext.properties().containsKey(START_TIME))
        {
            throw new IllegalArgumentException(START_TIME + " is missing in properties " + filterContext.propertiesName());
        }

        nextIntervalWithConfig(timerConfigOf(timerID,
                Instant.parse(filterContext.properties().getProperty(START_TIME) )),
                filterContext,
                outputPipe);
    }

    public static void nextIntervalWithConfig(TimerConfig timerConfig, FilterContext filterContext, OutputPipe<TimeInterval> outputPipe)
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

        //2. Forward timeInterval. Only in case of success, we store the new value
        try {
            outputPipe.forward(timerState.timerInterval);
        } catch (ProcessingException e) {
            SLF4jLogger.getLogger(PersistentTimer.class).warn("Error on processing timerId {}, timeInterval {} -> Repeat interval with last start-time until success", timerState.timerID(), timerState.timerInterval);
            throw e;
        }

        //3. Store the new TimeInterval in Repository
        if (repository.get(timerConfig.timerID()).isPresent())
        {
            repository.update(timerState);
        } else {
            repository.add(timerState);
        }
    }


    private record TimerState(TimerID timerID, TimeInterval timerInterval){}
}
