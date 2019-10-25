package comparingMapPerformance;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import static java.lang.String.format;
import static java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;
import static java.util.concurrent.TimeUnit.SECONDS;

public class ThroughputTester {
    private static final long WARMUP_DURATION = 2000;
    private final Map map;
    private final int testDuration;
    private final AtomicInteger counter;
    private final ExecutorService executorService;
    private final TaskFactory taskFactory;
    private final TimedRunner warmupRunner;
    private final TimedRunner timedRunner;
    private final static Logger LOGGER = Logger.getLogger(ThroughputTester.class.getSimpleName());

    ThroughputTester(Map map, ExecutorService executorService, int testDuration, AtomicInteger counter, TaskFactory taskFactory, TimedRunner warmupRunner, TimedRunner timedRunner) {
        this.map = map;
        this.executorService = executorService;
        this.testDuration = testDuration;
        this.counter = counter;
        this.taskFactory = taskFactory;
        this.warmupRunner = warmupRunner;
        this.timedRunner = timedRunner;
    }

    public float throughput() {
        executeOperations();
        float result = counter.floatValue() / testDuration;
        LOGGER.fine(format("%s Result = %s", map.getClass(), result));
        return result;
    }

    private void executeOperations() {
        warmupRunner.run(runnable(new AtomicInteger()));
        timedRunner.run(runnable(counter));
        executorService.shutdownNow();
    }

    private Runnable runnable(AtomicInteger counter) {
        return () -> executorService.execute(taskFactory.task(map, counter));
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

        return new ThroughputTester(map, executorService, testDurationMillis, new AtomicInteger(), TaskFactory.from(), TimedRunner.from(WARMUP_DURATION), TimedRunner.from(testDurationMillis));
    }
}
