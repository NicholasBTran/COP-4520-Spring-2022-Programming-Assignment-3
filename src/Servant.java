import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Servant extends Thread {
    private final static int NUM_PRESENTS = 500000;
    static LockFreeList<Integer> chain = new LockFreeList<>();
    static ArrayList<Integer> orderedBag = new ArrayList<>();
    static ArrayBlockingQueue<Integer> unorderedBag = new ArrayBlockingQueue<>(NUM_PRESENTS);
    static ArrayBlockingQueue<Integer> removeList = new ArrayBlockingQueue<>(NUM_PRESENTS);
    static AtomicInteger numThankYous = new AtomicInteger(0);
    private static final Random random = new Random();

    public static void createBag() {
        // Create an unordered bag of presents
        for (int i = 0; i < NUM_PRESENTS; i++) {
            orderedBag.add(i);
        }
        Collections.shuffle(orderedBag);

        for (int i = 0; i < NUM_PRESENTS; i++) {
            unorderedBag.add(orderedBag.get(i));
        }
    }

    public void run() {
        int randomInt;

        // Keep working until the bag is empty, and all thanks you's have been written.
        while (!unorderedBag.isEmpty() || numThankYous.get() < NUM_PRESENTS) {
            randomInt = random.nextInt(0, 3);

            // The servants will randomly decide to add, remove, or check if a present is in the chain.
            switch (randomInt) {
                case 0:
                    // Add present to chain
                    if (!unorderedBag.isEmpty()) {
                        Integer present = unorderedBag.poll();
                        if (present != null) {
                            chain.add(present);
                            removeList.add(present);
                        }
                    }
                    break;
                case 1:
                    // Write "Thank you" card, and remove present from chain
                    if (!chain.isEmpty()) {
                        // randomInt = random.nextInt(0, NUM_PRESENTS);
                        Integer toRemove = removeList.poll();
                        if (toRemove != null && chain.remove(toRemove)) {
                            numThankYous.getAndIncrement();
                        } else if (toRemove != null) {
                            removeList.add(toRemove);
                        }
                    }
                    break;
                case 2:
                    // Check if gift is in the chain
                    randomInt = random.nextInt(0, NUM_PRESENTS);
                    chain.contains(randomInt);
                    break;
            }
        }
        System.out.println("Servant Done.");
    }
}
