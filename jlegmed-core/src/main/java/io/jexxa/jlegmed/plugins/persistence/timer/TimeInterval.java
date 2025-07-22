package io.jexxa.jlegmed.plugins.persistence.timer;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public record TimeInterval(Instant begin,
                           Instant end)
{
    public TimeInterval (Instant begin, Instant end){
        this.begin = Objects.requireNonNull(begin);
        this.end = Objects.requireNonNull(end);
        if (end.compareTo(begin) < 0) {
            throw new IllegalArgumentException("end less than begin");
        }
    }

    public TimeInterval(Instant begin) {
        this(begin, Instant.now());
    }

    public LocalDateTime begin(ZoneId zoneId, ChronoUnit unit) {
        return begin().atZone(zoneId).truncatedTo(unit).toLocalDateTime();
    }
    public LocalDateTime end(ZoneId zoneId, ChronoUnit unit) {
        return end().atZone(zoneId).truncatedTo(unit).toLocalDateTime();
    }

}
