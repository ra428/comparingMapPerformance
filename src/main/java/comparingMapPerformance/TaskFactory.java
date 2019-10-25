package comparingMapPerformance;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class TaskFactory {

    private final static Logger LOGGER = Logger.getLogger(TaskFactory.class.getSimpleName());
    private final Random random;

    TaskFactory(Random random) {
        this.random = random;
    }

    Runnable task(Map<Integer, Integer> map, AtomicInteger counter) {
        return () -> {
//            LOGGER.fine(format("<%s> Executing task", Thread.currentThread().getName()));
            int anInt = random.nextInt(100);

            if (map.containsKey(anInt)) {
                if (anInt <= 2) {
//                    LOGGER.fine(format("<%s> Removing %s", Thread.currentThread().getName(), anInt));
                    map.remove(anInt);
                }
            } else {
                if (anInt <= 60) {
//                    LOGGER.fine(format("<%s> Putting %s", Thread.currentThread().getName(), anInt));
                    map.put(anInt, anInt);
                }
            }
            counter.incrementAndGet();
        };
    }

    static TaskFactory from() {
        return new TaskFactory(new Random());
    }
}
