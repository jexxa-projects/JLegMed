package io.jexxa.jlegmed.plugins.persistence.timer;

import java.util.Objects;

public record TimerID(String timerID) {
    public TimerID(String timerID)
    {
        this.timerID = Objects.requireNonNull(timerID);
    }

    public static TimerID timerIdOf(String timerID)
    {
        return new TimerID(timerID);
    }
}
