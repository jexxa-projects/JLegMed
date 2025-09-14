package io.jexxa.jlegmed.plugins.persistence.timer;

import io.jexxa.common.facade.logger.SLF4jLogger;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.filter.ProcessingException;
import io.jexxa.jlegmed.core.pipes.OutputPipe;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.jexxa.jlegmed.plugins.persistence.repository.RepositoryPool.getRepository;
import static io.jexxa.jlegmed.plugins.persistence.timer.TimerConfig.timerConfigOf;

public class PersistentTimer {
    public static final String START_TIME = "start.time";
    public static final String LOOKBACK_PERIOD = "lookback.period";
    public static void nextInterval(TimerID timerID, FilterContext filterContext, OutputPipe<TimeInterval> outputPipe) {
        Instant startTime = initialStartTime(filterContext);

        nextIntervalWithConfig(timerConfigOf(timerID, startTime),
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

    private static Instant initialStartTime(FilterContext filterContext)
    {
        if (filterContext.properties().containsKey(START_TIME))
        {
            return Instant.parse(filterContext.properties().getProperty(START_TIME) );
        }

        if (filterContext.properties().containsKey(LOOKBACK_PERIOD))
        {
            return Instant.now().minus(lookBackPeriod(filterContext.properties().getProperty(LOOKBACK_PERIOD)));
        }

        throw new IllegalArgumentException(START_TIME + " or " + LOOKBACK_PERIOD + " is missing in properties " + filterContext.propertiesName());
    }

    /**
     * Parst einen einfachen Zeitstring wie "5d", "3h", "10m", "2w"
     * und liefert eine Duration oder Period zurück.
     */
    private static final Pattern PATTERN = Pattern.compile("(\\d+)([smhdw])");

    public static TemporalAmount lookBackPeriod(String input) {
        Matcher m = PATTERN.matcher(input.trim().toLowerCase());
        if (!m.matches()) {
            SLF4jLogger.getLogger(PersistentTimer.class).error("Invalid format: {}", input);
            SLF4jLogger.getLogger(PersistentTimer.class).error("Valid format specification are 's', 'm', 'h', 'd', 'w'. Example:" );
            SLF4jLogger.getLogger(PersistentTimer.class).error(" 5s -> 5 seconds" );
            SLF4jLogger.getLogger(PersistentTimer.class).error(" 5m -> 5 minutes" );
            SLF4jLogger.getLogger(PersistentTimer.class).error(" 5h -> 5 hours" );
            SLF4jLogger.getLogger(PersistentTimer.class).error(" 5d -> 5 days" );
            SLF4jLogger.getLogger(PersistentTimer.class).error(" 5w -> 5 weeks" );
            throw new IllegalArgumentException("Invalid format: " + input );
        }

        int value = Integer.parseInt(m.group(1));
        String unit = m.group(2);

        return switch (unit) {
            case "s" -> Duration.ofSeconds(value);
            case "m" -> Duration.ofMinutes(value);
            case "h" -> Duration.ofHours(value);
            case "d" -> Duration.ofDays(value);   // Tage → Period
            case "w" -> Duration.ofDays(value * 7L);  // Wochen → Period
            default -> throw new IllegalArgumentException("Invalid timeunit: " + unit);
        };
    }

    private record TimerState(TimerID timerID, TimeInterval timerInterval){}
}
