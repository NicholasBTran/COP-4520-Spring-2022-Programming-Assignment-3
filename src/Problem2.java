// Nicholas Tran
// COP 4520 - Parallel Programming
// Spring 2022
// Programming Assignment 3, Problem 2: Atmospheric Temperature Reading Module

import java.util.ArrayList;

public class Problem2 {
    private final static int THREADS = 8;

    public static void main(String[] args) {
        long startTime, stopTime, elapsedTime;
        startTime = System.currentTimeMillis();
        ArrayList<Sensor> thread = new ArrayList<>();

        // Sensor setup
        Sensor.setupList();

        // Create sensor threads
        Sensor.LeadSensor s = new Sensor.LeadSensor();
        s.setName("Sensor-" + 0);
        thread.add(s);
        for (int i = 1; i < THREADS; i++) {
            Sensor sensor = new Sensor();
            sensor.setName("Sensor-" + i);
            thread.add(sensor);
        }

        for (int i = 0; i < THREADS; i++) {
            thread.get(i).start();
        }

        for (int i = 0; i < THREADS; i++) {
            try {
                thread.get(i).join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        stopTime = System.currentTimeMillis();
        elapsedTime = stopTime - startTime;
        System.out.println("Elapsed Time (ms): " + elapsedTime);
    }
}
