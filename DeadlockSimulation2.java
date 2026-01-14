import java.util.concurrent.Semaphore;

class Account {
    public int id;
    public double balance;
    public Semaphore lock = new Semaphore(1);

    public Account(int id, double balance) {
        this.id = id;
        this.balance = balance;
    }
}

public class DeadlockSimulation2 {

    public static void transfer(Account from, Account to, double amount) {

        Account firstLock = from.id < to.id ? from : to;
        Account secondLock = from.id < to.id ? to : from;
        try {
            System.out.println(Thread.currentThread().getName() + " trying to lock Account " + from.id);
            firstLock.lock.acquire(); 
            System.out.println(Thread.currentThread().getName() + " locked Account " + from.id);

            System.out.println(Thread.currentThread().getName() + " processing... (holding lock on " + from.id + ")");
            Thread.sleep(1000); 

            System.out.println(Thread.currentThread().getName() + " trying to lock Account " + to.id);
            secondLock.lock.acquire(); 
            
            System.out.println(Thread.currentThread().getName() + " locked Account " + to.id);

            if (from.balance >= amount) {
                from.balance -= amount;
                to.balance += amount;
                System.out.println("Success! Transferred " + amount + " from " + from.id + " to " + to.id);
            } else {
                System.out.println("Insufficient funds for " + Thread.currentThread().getName());
            }

            secondLock.lock.release();
            firstLock.lock.release();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Account account1 = new Account(1, 1000);
        Account account2 = new Account(2, 1000);

        Thread t1 = new Thread(() -> {
            transfer(account1, account2, 100);
        });

        Thread t2 = new Thread(() -> {
            transfer(account2, account1, 100);
        });
        
        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Main finished."); 
    }
}