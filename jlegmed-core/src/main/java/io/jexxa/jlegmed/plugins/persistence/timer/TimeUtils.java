package io.jexxa.jlegmed.plugins.persistence.timer;

import java.time.Duration;

public class TimeUtils {
    public static TimeInterval startWithLookBack(TimeInterval timeInterval, Duration duration) {
        return new TimeInterval(timeInterval.begin().minus(duration), timeInterval.end());
    }

    public static TimeInterval endWithLookAhead(TimeInterval timeInterval, Duration duration) {
        return new TimeInterval(timeInterval.begin(), timeInterval.end().plus(duration));
    }

}
