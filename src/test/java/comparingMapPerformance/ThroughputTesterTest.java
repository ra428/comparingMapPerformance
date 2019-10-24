package comparingMapPerformance;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.time.Clock;
import java.time.Instant;
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
    private Clock clock = mock(Clock.class);
    private AtomicInteger counter = new AtomicInteger();

    @Test
    public void counterCountsMapOperations() {
        new ThroughputTester(map, executorService, 0, counter, clock, taskFactory).throughput();

        verify(map, times(counter.get())).get(any());
    }

    @Test
    public void throughputEqualsCountDividedByDuration() {
        int testDuration = 10;
        float throughput = new ThroughputTester(map, executorService, testDuration, counter, clock, taskFactory).throughput();

        assertThat(throughput, is(equalTo(counter.floatValue() / testDuration)));
    }

    @Test
    public void executesTaskWhileWithinTestDuration() {
        final int noOfCalls = 5;

        doAnswer(new Answer<Instant>() {
            private int count = 0;

            public Instant answer(InvocationOnMock invocation) {
                count++;
                if (count > noOfCalls)
                    return Instant.parse("2007-12-04T00:00:00.00Z");
                return Instant.parse("2007-12-03T00:00:00.00Z");
            }
        }).when(clock).instant();

        new ThroughputTester(map, executorService, 1, counter, clock, taskFactory).throughput();

        verify(executorService, times(noOfCalls)).execute(any());
        verify(executorService, times(1)).shutdownNow();
    }

    @Before
    public void setUp() {
        doAnswer(emptyRunnable()).when(taskFactory).task(anyMap(), any());
        doAnswer(executeRunnable()).when(executorService).execute(any());

        doAnswer(new Answer<Instant>() {
            private int count = 0;

            public Instant answer(InvocationOnMock invocation) {
                count++;
                if (count == 1)
                    return Instant.parse("2007-12-03T00:00:00.00Z");
                return Instant.parse("2007-12-04T00:00:00.00Z");
            }
        }).when(clock).instant();
    }

    private Answer executeRunnable() {
        return i -> {
            ((Runnable) i.getArguments()[0]).run();
            return null;
        };
    }

    private Answer<Runnable> emptyRunnable() {
        return i -> () -> {
        };
    }
}
