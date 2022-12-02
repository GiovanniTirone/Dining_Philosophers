import java.util.Arrays;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class Test {
    public static void main(String[] args) throws InterruptedException {

        ReentrantLock critical_region_mtx = new ReentrantLock();
        ReentrantLock output_mtx = new ReentrantLock();

        Semaphore both_forks_available[] = new Semaphore[5];
        for(int i=0; i<5; i++){
            both_forks_available[i] = new Semaphore(1);
        }

        Philosopher philosophers [] = new Philosopher[5];
        for(int i=0; i<5; i++){
            philosophers[i] = new Philosopher(i,
                                                output_mtx,
                                                critical_region_mtx,
                                                both_forks_available,
                                                philosophers);
        }

        Thread threads [] = new Thread[5];
        for(int i=0; i<5; i++){
            threads[i] = new Thread(philosophers[i]);
            threads[i].start();
        }

        Thread.sleep(5000);

        for(Philosopher philosopher : philosophers)
            philosopher.setFull(true);

        for(int i=0; i<5; i++){
            threads[i].join();
        }

        for(Philosopher philosopher : philosophers)
            System.out.println("The " + philosopher + " has eaten : " + philosopher.getEatingCounter());

    }
}