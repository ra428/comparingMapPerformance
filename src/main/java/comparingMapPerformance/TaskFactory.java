package comparingMapPerformance;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import static java.lang.String.format;

public class TaskFactory {

    private final static Logger LOGGER = Logger.getLogger(TaskFactory.class.getSimpleName());
    private final Random random;

    TaskFactory(Random random) {
        this.random = random;
    }

    Runnable task(Map<Integer, Integer> map, AtomicInteger counter) {
        return () -> {
            LOGGER.fine(format("<%s> Executing task", Thread.currentThread().getName()));
            int anInt = random.nextInt(10);

            if (map.containsKey(anInt)) {
                if (anInt > 8) map.remove(anInt);
            } else {
                if (anInt >= 4) map.put(anInt, anInt);
            }
            counter.incrementAndGet();
        };
    }

    static TaskFactory from() {
        return new TaskFactory(new Random());
    }
}
