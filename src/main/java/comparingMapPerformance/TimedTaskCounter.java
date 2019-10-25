package comparingMapPerformance;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.ToIntFunction;
import java.util.logging.Logger;

class TimedTaskCounter {

    private final Clock clock;
    private final long durationInMillis;
    private final static Logger LOGGER = Logger.getLogger(TimedTaskCounter.class.getSimpleName());

    TimedTaskCounter(Clock clock, long durationInMillis) {
        this.clock = clock;
        this.durationInMillis = durationInMillis;
    }

    Integer run(Callable<Integer> callable, ExecutorService executorService) {
        List<Future<Integer>> counts = new ArrayList<>();

        Instant finishTime = clock.instant().plusMillis(durationInMillis);
        do {
            collectFutures(callable, counts, executorService);
        } while (clock.instant().isBefore(finishTime));
        executorService.shutdownNow();

        LOGGER.fine("Summing results");

        return counts.size();

//        return counts.parallelStream()
//                .filter(Objects::nonNull)
//                .peek(f -> System.out.println(f.toString()))
//                .mapToInt(getInt())
//                .sum();
    }

    private void collectFutures(Callable<Integer> callable, List<Future<Integer>> counts, ExecutorService executorService) {
        try {
            counts.add(executorService.submit(callable));
        } catch (Exception e) {
            System.out.println("Oh no");
        }
    }

    private ToIntFunction<Future<Integer>> getInt() {
        return f -> {
            try {
                return f.get();
            } catch (Exception e) {
                return 0;
            }
        };
    }

    static TimedTaskCounter from(long durationInMillis) {
        return new TimedTaskCounter(Clock.systemUTC(), durationInMillis);
    }
}
