package comparingMapPerformance;

public class App {
    public static void main(String[] args) {
        int testDurationInMillis = 1000;
        Scenarios.from(testDurationInMillis).run();
    }
}
