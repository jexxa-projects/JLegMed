package io.jexxa.jlegmed.plugins.persistence.timer;

import io.jexxa.jlegmed.core.filter.FilterContext;
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
    public static final String WINDOW_MAX_SIZE = "window.max.size";
    public static void nextInterval(TimerID timerID, FilterContext filterContext, OutputPipe<TimeInterval> outputPipe) {
        Instant startTime = initialStartTime(filterContext);

        nextIntervalWithConfig(timerConfigOf(timerID, startTime),
                filterContext,
                outputPipe);
    }

    public static void nextIntervalWithConfig(TimerConfig timerConfig, FilterContext filterContext, OutputPipe<TimeInterval> outputPipe) {
        var repository = getRepository(TimerState.class, TimerState::timerID, filterContext);

        // 1. Configuration & Start point
        Duration maxWindowSize = parsePeriod(
                filterContext.properties().getProperty(WINDOW_MAX_SIZE, "PT24H")
        );
        Instant start = repository.get(timerConfig.timerID())
                .map(state -> state.timerInterval.end())
                .orElse(timerConfig.startTime());

        Instant now = Instant.now();
        if (!start.isBefore(now)) {
            return; // Up to date -> Nothing to do
        }

        // 2. Calculate the next interval
        Instant requestedEnd = start.plus(maxWindowSize);
        Instant end = requestedEnd.isBefore(now) ? requestedEnd : now;
        TimerState timerState = new TimerState(timerConfig.timerID(), new TimeInterval(start, end));

        // 3. Forward and store timerInterval
        outputPipe.forward(timerState.timerInterval);
        repository.put(timerState);

        // 4. If we are not completely caught up, process again
        if (end.isBefore(now)) {
            filterContext.processingState().processAgain();
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
        return parsePeriod(input);
    }

    private static Duration parsePeriod(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Time string must not be null or empty");
        }

        String trimmedInput = input.trim();

        // 1. ISO-8601 period starts always with 'P'
        if (trimmedInput.toUpperCase().startsWith("P")) {
            return parseIsoFormat(trimmedInput);
        }

        // 2. Custom Format (z.B. 5h, 10d)
        return parseCustomFormat(trimmedInput);
    }

    private static Duration parseIsoFormat(String input) {
        return Duration.parse(input);
    }

    private static Duration parseCustomFormat(String input) {
        Matcher m = PATTERN.matcher(input.toLowerCase());
        if (!m.matches()) {
            throw new IllegalArgumentException("Invalid custom format: " + input);
        }

        int value = Integer.parseInt(m.group(1));
        String unit = m.group(2);

        return switch (unit) {
            case "s" -> Duration.ofSeconds(value);
            case "m" -> Duration.ofMinutes(value);
            case "h" -> Duration.ofHours(value);
            case "d" -> Duration.ofDays(value);
            case "w" -> Duration.ofDays(value * 7L);
            default -> throw new IllegalArgumentException("Unexpected unit: " + unit);
        };
    }


    private record TimerState(TimerID timerID, TimeInterval timerInterval){}
}
