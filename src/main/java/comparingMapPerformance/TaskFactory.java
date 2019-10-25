package comparingMapPerformance;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

public class TaskFactory {

    private final Random random;
    private final static Logger LOGGER = Logger.getLogger(TaskFactory.class.getSimpleName());

    TaskFactory(Random random) {
        this.random = random;
    }

    Callable<Integer> task(Map<Integer, Integer> map) {
        return () -> {
            LOGGER.fine("Running task");
            int count = 1;
            int anInt = random.nextInt(100);

            if (map.containsKey(anInt)) {
                if (anInt <= 2) {
                    map.remove(anInt);
                    count++;
                }
            } else {
                if (anInt <= 60) {
                    map.put(anInt, anInt);
                    count++;
                }
            }
            return count;
        };
    }

    static TaskFactory from() {
        return new TaskFactory(new Random());
    }
}
