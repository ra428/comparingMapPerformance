package comparingMapPerformance;

import java.time.Clock;
import java.time.Instant;

class TimedRunner {

    private final Clock clock;
    private final long durationInMillis;

    TimedRunner(Clock clock, long durationInMillis) {
        this.clock = clock;
        this.durationInMillis = durationInMillis;
    }

    void run(Runnable runnable) {
        Instant finishTime = clock.instant().plusMillis(durationInMillis);
        do {
            runnable.run();
        } while (clock.instant().isBefore(finishTime));
    }

    static TimedRunner from(long durationInMillis) {
        return new TimedRunner(Clock.systemUTC(), durationInMillis);
    }
}
