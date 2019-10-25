package comparingMapPerformance;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

class Scenarios {

    private final int testDuration;
    private final List<Map> maps;
    private final List<Integer> threads;
    private final static Logger LOGGER = Logger.getLogger(Scenarios.class.getSimpleName());

    Scenarios(List<Map> maps, List<Integer> threads, int testDuration) {
        this.testDuration = testDuration;
        this.maps = maps;
        this.threads = threads;
    }

    void run() {
        Map<String, List<Float>> results = maps.stream().collect(toMap(m -> m.getClass().getSimpleName() + m.hashCode(), this::computeThroughputs));
        LOGGER.info(format("Results = %s", results));
    }

    private List<Float> computeThroughputs(Map m) {
        return threads.stream()
                .map(t -> ThroughputTester.from(m, testDuration, t).throughput())
                .collect(toList());
    }

    private static List<Integer> threads() {
        return Arrays.asList(64,32,16,8,4,2,1);
    }

    private static List<Map> maps() {
        return Arrays.asList(
                new HashMap()
//                new ConcurrentHashMap()
//                new ConcurrentHashMap<>(),
//                synchronizedMap(new HashMap<>())
//                new ConcurrentSkipListMap<>()
        );
    }

    static Scenarios from(int testDuration) {
        return new Scenarios(maps(), threads(), testDuration);
    }
}
