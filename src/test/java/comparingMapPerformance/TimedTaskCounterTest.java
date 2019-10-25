package comparingMapPerformance;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import static org.mockito.Mockito.*;

public class TimedTaskCounterTest {

    private Clock clock = mock(Clock.class);
    private Callable<Integer> callable = mock(Callable.class);
    private ExecutorService executorService = mock(ExecutorService.class);
    private int duration = 1;
    private TimedTaskCounter timedTaskCounter = new TimedTaskCounter(clock, duration);

    @Test
    public void runsForDuration() {
        int noOfCalls = 5;
        timeoutIn(noOfCalls);

        timedTaskCounter.run(callable, executorService);

        verify(executorService, times(noOfCalls)).submit(callable);
        verify(executorService, times(1)).shutdownNow();
    }

    @Test
    public void runsAtLeastOnce() {
        timeoutIn(1);

        timedTaskCounter.run(callable, executorService);

        verify(executorService, times(1)).submit(callable);
        verify(executorService, times(1)).shutdownNow();
    }

    private void timeoutIn(int noOfCalls) {
        Instant currentTime = Instant.parse("2000-01-01T00:00:00.00Z");
        Instant futureTime = Instant.parse("2020-01-01T00:00:00.00Z");

        doAnswer(new Answer<Instant>() {
            private int count = 0;

            public Instant answer(InvocationOnMock invocation) {
                return count++ == noOfCalls ? futureTime : currentTime;
            }
        }).when(clock).instant();
    }
}
