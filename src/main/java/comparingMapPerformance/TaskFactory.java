package comparingMapPerformance;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import static java.lang.String.format;

public class TaskFactory {

    private final static Logger LOGGER = Logger.getLogger(TaskFactory.class.getSimpleName());
    private final Random random;

    public TaskFactory(Random random) {
        this.random = random;
    }

    Runnable task(Map map, AtomicInteger counter) {
        return () -> {
            LOGGER.fine(format("<%s> Executing task", Thread.currentThread().getName()));
            Object object = new Object();
            int r = random.nextInt(10);

            if (map.containsKey(object.hashCode())) {
                if (r > 9) map.remove(object.hashCode());
            } else {
                if (r > 4) map.put(object.hashCode(), object);
            }
            counter.incrementAndGet();
        };
    }

    static TaskFactory from() {
        return new TaskFactory(new Random());
    }
}
