import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Sensor extends Thread {
    static final Random random = new Random();
    private final static int HOURS_TO_RUN = 5;
    private final static int WAIT_TIME = 20;
    static ArrayList<LockFreeListDoubles<Double>> readings = new ArrayList<>();
    static ArrayList<LockFreeListDoubles<Double>> tenMinuteReadings = new ArrayList<>();
    static ArrayList<ArrayList<Double>> tenMinuteDiff = new ArrayList<>();

    public static void setupList() {
        for (int i = 0; i < HOURS_TO_RUN; i++) {
            readings.add(new LockFreeListDoubles<Double>());
        }

        for (int i = 0; i < HOURS_TO_RUN; i++) {
            for (int j = 0; j < 6; j++) {
                tenMinuteReadings.add(new LockFreeListDoubles<Double>());
            }
            tenMinuteDiff.add(new ArrayList<Double>());
        }
    }

    protected double randomReading() {
        return Math.floor(random.nextDouble(-100, 70) * 1000) / 1000;
    }

    public void run() {
        for (int i = 0; i < HOURS_TO_RUN; i++) {
            for (int j = 0; j < 6; j++) { // Every minute
                for (int k = 0; k < 10; k++) { // Every minute
                    Double reading = randomReading();
                    readings.get(i).add(reading);
                    tenMinuteReadings.get(j).add(reading);
                    // Wait a minute
                    try {
                        Thread.sleep(WAIT_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static class LeadSensor extends Sensor {
        protected void createHourlyReport(int hour) {
            LockFreeListDoubles<Double> hourlyReadings = readings.get(hour);
            double[] highs = hourlyReadings.fiveHigest(8 * 60 - 10);
            double[] lows = hourlyReadings.fiveLowest();
            ArrayList<Double> list = tenMinuteDiff.get(hour);

            System.out.print("Hi for hour " + hour + ": ");
            for (int i = 0; i < 5; i++) {
                System.out.print(highs[i] + " ");
            }
            System.out.println();

            System.out.print("Lo for hour " + hour + ": ");
            for (int i = 0; i < 5; i++) {
                System.out.print(lows[i] + " ");
            }
            System.out.println();
            double high = Collections.max(list);
            int index = list.indexOf(high);
            System.out.println("Largest difference for hour " + hour + ": " + high + " at " + index * 10 + " to " + (index + 1) * 10 + " minutes.\n");
        }

        protected void createTenMinuteReport(int hours, int tenMinutes) {
            LockFreeListDoubles<Double> minutelyReadings = tenMinuteReadings.get(tenMinutes);
            double[] highs = minutelyReadings.fiveHigest(8 * 60 - 5);
            double[] lows = minutelyReadings.fiveLowest();
            ArrayList<Double> list = tenMinuteDiff.get(hours);

            System.out.print("Hi for hour " + hours + " minutes " + (tenMinutes * 10) + ": ");
            for (int i = 0; i < 5; i++) {
                System.out.print(highs[i] + " ");
            }
            System.out.println();

            System.out.print("Ho for hour " + hours + " minutes " + (tenMinutes * 10) + ": ");
            for (int i = 0; i < 5; i++) {
                System.out.print(lows[i] + " ");
            }
            System.out.println();

            // Calculate the difference between the highest and lowest readings
            list.add(Math.floor((highs[4] - lows[0]) * 1000) / 1000);
        }

        public void run() {
            for (int i = 0; i < HOURS_TO_RUN; i++) {
                for (int j = 0; j < 6; j++) { // Every minute
                    for (int k = 0; k < 10; k++) { // Every minute
                        double reading = randomReading();
                        readings.get(i).add(reading);
                        tenMinuteReadings.get(j).add(reading);
                        // Wait a minute
                        try {
                            Thread.sleep(WAIT_TIME);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    createTenMinuteReport(i, j);
                } createHourlyReport(i);
            }
        }
    }
}
