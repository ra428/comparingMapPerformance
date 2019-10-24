package comparingMapPerformance;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.time.Clock;
import java.time.Instant;

import static org.mockito.Mockito.*;

public class TimedRunnerTest {

    private Clock clock = mock(Clock.class);
    private Runnable runnable = mock(Runnable.class);
    private int duration = 1;
    private TimedRunner timedRunner = new TimedRunner(clock, duration);

    @Test
    public void runsForDuration() {
        int noOfCalls = 5;
        timeoutIn(noOfCalls);

        timedRunner.run(runnable);

        verify(runnable, times(noOfCalls)).run();
    }

    @Test
    public void runsAtLeastOnce() {
        timeoutIn(1);

        timedRunner.run(runnable);

        verify(runnable, times(1)).run();
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
