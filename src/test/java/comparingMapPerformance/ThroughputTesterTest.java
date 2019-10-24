package comparingMapPerformance;

import org.junit.Test;
import org.mockito.stubbing.Answer;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

public class ThroughputTesterTest {

    private ExecutorService executorService = mock(ExecutorService.class);
    private TaskFactory taskFactory = mock(TaskFactory.class);
    private Map map = mock(Map.class);
    private AtomicInteger counter = new AtomicInteger();
    private TimedRunner warmupRunner = mock(TimedRunner.class);
    private TimedRunner timedRunner = mock(TimedRunner.class);
    private int testDuration = 1;
    private ThroughputTester throughputTester = new ThroughputTester(map, executorService, testDuration, counter, taskFactory, warmupRunner, timedRunner);

    @Test
    public void throughputEqualsCountDividedByDuration() {
        counter = new AtomicInteger(5);
        throughputTester = new ThroughputTester(map, executorService, testDuration, counter, taskFactory, warmupRunner, timedRunner);
        float throughput = throughputTester.throughput();

        assertThat(throughput, is(equalTo(counter.floatValue() / testDuration)));
    }

    @Test
    public void executesWarmupAndTimedRunnables() {
        throughputTester.throughput();

        verify(warmupRunner, times(1)).run(any());
        verify(timedRunner, times(1)).run(any());
        verify(executorService, times(1)).shutdownNow();
    }

    @Test
    public void warmupDoesNotIncrementCounter() {
        doAnswer(executeRunnable()).when(warmupRunner).run(any());

        float throughput = throughputTester.throughput();

        assert throughput == 0f;
    }

    @Test
    public void timedRunnerUsesExecutorServiceToIncrementCounter() {
        doAnswer(executeRunnable()).when(timedRunner).run(any());
        doAnswer((Answer<Void>) invocation -> {
            counter.incrementAndGet();
            return null;
        }).when(executorService).execute(any());

        float throughput = throughputTester.throughput();

        assert throughput == 1f;
    }

    private Answer executeRunnable() {
        return i -> {
            ((Runnable) i.getArguments()[0]).run();
            return null;
        };
    }
}
