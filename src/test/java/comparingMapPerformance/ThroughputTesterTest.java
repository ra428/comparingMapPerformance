package comparingMapPerformance;

import org.junit.Test;

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
    private TimedTaskCounter timedTaskCounter = mock(TimedTaskCounter.class);
    private int testDuration = 1;
    private ThroughputTester throughputTester = new ThroughputTester(map, executorService, testDuration, taskFactory, warmupRunner, timedTaskCounter);

    @Test
    public void throughputEqualsCountDividedByDuration() {
        doAnswer(i -> 1).when(timedTaskCounter).run(any(), any());
        throughputTester = new ThroughputTester(map, executorService, testDuration, taskFactory, warmupRunner, timedTaskCounter);
        float throughput = throughputTester.throughput();

        assertThat(throughput, is(equalTo(1f)));
    }

    @Test
    public void executesWarmupAndTimedTasks() {
        throughputTester.throughput();

        verify(warmupRunner, times(1)).run(any());
        verify(timedTaskCounter, times(1)).run(any(),any() );
    }

    @Test
    public void warmupDoesNotIncrementCounter() {
        doAnswer(i -> 1).when(warmupRunner).run(any());

        float throughput = throughputTester.throughput();

        assert throughput == 0f;
    }

    @Test
    public void timedRunnerUsesExecutorServiceToIncrementCounter() {
        doAnswer(i -> 1).when(timedTaskCounter).run(any(), any());

        float throughput = throughputTester.throughput();

        assert throughput == 1f;
    }
}
