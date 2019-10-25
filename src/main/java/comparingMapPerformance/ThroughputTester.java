package comparingMapPerformance;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Logger;

import static java.lang.String.format;
import static java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;
import static java.util.concurrent.TimeUnit.SECONDS;

public class ThroughputTester {
    private static final long WARMUP_DURATION = 2000;
    private final Map map;
    private final int testDuration;
    private final ExecutorService executorService;
    private final TaskFactory taskFactory;
    private final TimedRunner warmupRunner;
    private final TimedTaskCounter timedTaskCounter;
    private final static Logger LOGGER = Logger.getLogger(ThroughputTester.class.getSimpleName());

    ThroughputTester(Map map, ExecutorService executorService, int testDuration, TaskFactory taskFactory, TimedRunner warmupRunner, TimedTaskCounter timedTaskCounter) {
        this.map = map;
        this.executorService = executorService;
        this.testDuration = testDuration;
        this.taskFactory = taskFactory;
        this.warmupRunner = warmupRunner;
        this.timedTaskCounter = timedTaskCounter;
    }

    public float throughput() {
        int count = executeOperations();
        float result = ((float) count) / testDuration;
        LOGGER.fine(format("%s Result = %s", map.getClass(), result));
        return result;
    }

    private Integer executeOperations() {
        warmupRunner.run(() -> taskFactory.task(map));
        return timedTaskCounter.run(taskFactory.task(map), executorService);
    }

    static ThroughputTester from(Map map, int testDurationMillis, int numberOfThreads) {
        int keepAliveTime = 10;
        int capacity = 100;
        int maximumPoolSize = numberOfThreads;
        ExecutorService executorService = new ThreadPoolExecutor(
                numberOfThreads,
                maximumPoolSize,
                keepAliveTime,
                SECONDS,
                new ArrayBlockingQueue<>(capacity),
                new DiscardPolicy()
        );

        return new ThroughputTester(map, executorService, testDurationMillis, TaskFactory.from(), TimedRunner.from(WARMUP_DURATION), TimedTaskCounter.from(testDurationMillis));
    }
}
