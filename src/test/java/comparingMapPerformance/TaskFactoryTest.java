package comparingMapPerformance;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
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

        int count = call(map);

        assertThat(map, is(anEmptyMap()));
        assert count == 2;
    }

    @Test
    public void addsElementWhenRandomIsLessThanSixty() {
        when(random.nextInt(100)).thenReturn(50);
        Map<Integer, Integer> map = new HashMap<>();

        int count = call(map);

        assertThat(map, is(not(anEmptyMap())));
        assert count == 2;
    }

    private Integer call(Map<Integer, Integer> map) {
        try {
            return taskFactory.task(map).call();
        } catch (Exception e) {
            return -1;
        }
    }
}
