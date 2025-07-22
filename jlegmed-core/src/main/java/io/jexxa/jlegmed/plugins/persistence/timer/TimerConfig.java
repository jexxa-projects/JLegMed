package io.jexxa.jlegmed.plugins.persistence.timer;

import java.time.Instant;
import java.util.Objects;

public record TimerConfig(TimerID timerID, Instant startTime) {
    public TimerConfig(TimerID timerID, Instant startTime)
    {
        this.timerID = Objects.requireNonNull(timerID);
        this.startTime = Objects.requireNonNull(startTime);
    }

    public static TimerConfig timerConfigOf(TimerID timerID)
    {
        return new TimerConfig(timerID, Instant.now());
    }

    public static TimerConfig timerConfigOf(TimerID timerID, Instant startTime)
    {
        return new TimerConfig(timerID, startTime);
    }

}
