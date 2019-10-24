package comparingMapPerformance;

public class App {
    public static void main(String[] args) {
        int testDurationInMillis = 10000;
        Scenarios.from(testDurationInMillis).run();
    }
}
