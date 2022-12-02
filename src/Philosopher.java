import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class Philosopher implements Runnable{

    int id;

    boolean full;
    ReentrantLock output_mtx;

    ReentrantLock critical_region_mtx;

    Semaphore [] both_forks_available;
    State state;

    Philosopher philosophers [];

    private Random random;

    private int eatingCounter;

    public Philosopher(int id,
                       ReentrantLock output_mtx,
                       ReentrantLock critical_region_mtx,
                       Semaphore[]both_forks_available,
                       Philosopher [] philosophers){
        this.id = id;
        this.full = false;
        this.random = new Random();
        this.output_mtx = output_mtx;
        this.critical_region_mtx = critical_region_mtx;
        this.both_forks_available = both_forks_available;
        this.philosophers = philosophers;
        this.eatingCounter = 0;
    }


    public void think () throws InterruptedException {
        int currentThinkingTime = random.nextInt(400)+400;
        output_mtx.lock();  //lock_guard???
        System.out.println(this + " is thinking for " + currentThinkingTime);
        output_mtx.unlock();
        Thread.sleep(currentThinkingTime);
    }

    public void take_forks () throws InterruptedException {
        critical_region_mtx.lock();
        this.state = State.HUNGRY;
        output_mtx.lock();
        System.out.println(this + " is in state HUNGRY");
        output_mtx.unlock();
        test(id);
        critical_region_mtx.unlock();
        both_forks_available[id].acquire();
    }

    public void put_forks () {
        critical_region_mtx.lock();
        this.state = State.THINKING;
        test(get_left_id(id));
        test(get_right_id(id));
        critical_region_mtx.unlock();
    }

    public void eat() throws InterruptedException {
        int currentEatingTime = random.nextInt(400)+400;
        output_mtx.lock();
        System.out.println(this + " is eating for " + currentEatingTime);
        eatingCounter++;
        output_mtx.unlock();
        Thread.sleep(currentEatingTime);
    }

    public void test (int test_id) {
        if(philosophers[test_id].getState() == State.HUNGRY &&
                philosophers[get_left_id(test_id)].getState() != State.EATING &&
                philosophers[get_right_id(test_id)].getState() != State.EATING ){
            philosophers[test_id].setState(State.EATING);
            both_forks_available[test_id].release();
        }
    }


    public int get_left_id (int central_id){
        return (central_id + 4) % 5;
    }

    public int get_right_id (int central_id){
        return (central_id +1) % 5;
    }

    @Override
    public String toString () {
        return "philosopher " + id;
    }



    @Override
    public void run() {
        while(!full){

            try {
                this.think();
                this.take_forks();
                this.eat();
                this.put_forks();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getEatingCounter() {
        return eatingCounter;
    }

    public void setFull(boolean full) {
        this.full = full;
    }
}
