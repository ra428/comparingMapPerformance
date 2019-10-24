package comparingMapPerformance;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.hamcrest.collection.IsMapWithSize.anEmptyMap;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TaskFactoryTest {

    private Random random = mock(Random.class);
    private TaskFactory taskFactory = new TaskFactory(random);

    @Test
    public void removesElementWhenRandomIsLessThanTwo() {
        when(random.nextInt(100)).thenReturn(2);
        Map<Integer, Integer> map = Stream.of(2).collect(toMap(identity(), identity()));

        runTask(map);

        assertThat(map, is(anEmptyMap()));
    }

    @Test
    public void addsElementWhenRandomIsLessThanSixty() {
        when(random.nextInt(100)).thenReturn(50);
        Map<Integer, Integer> map = new HashMap<>();

        runTask(map);

        assertThat(map, is(not(anEmptyMap())));
    }

    @Test
    public void incrementsCounter() {
        when(random.nextInt(100)).thenReturn(0);
        AtomicInteger counter = new AtomicInteger();

        for (int i = 1; i < 4; i++) {
            runTask(new HashMap<>(), counter);
            assert counter.get() == i;
        }
    }

    private void runTask(Map<Integer, Integer> map) {
        runTask(map, mock(AtomicInteger.class));
    }

    private void runTask(Map<Integer, Integer> map, AtomicInteger atomicInteger) {
        taskFactory.task(map, atomicInteger).run();
    }
}
