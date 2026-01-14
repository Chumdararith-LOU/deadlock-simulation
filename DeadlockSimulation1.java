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

public class DeadlockSimulation1 {

    public static void transfer(Account from, Account to, double amount) {
        try {
            System.out.println(Thread.currentThread().getName() + " trying to lock Account " + from.id);
            from.lock.acquire(); 
            System.out.println(Thread.currentThread().getName() + " locked Account " + from.id);

            System.out.println(Thread.currentThread().getName() + " processing... (holding lock on " + from.id + ")");
            Thread.sleep(1000); 

            System.out.println(Thread.currentThread().getName() + " trying to lock Account " + to.id);
            to.lock.acquire(); 
            
            System.out.println(Thread.currentThread().getName() + " locked Account " + to.id);

            if (from.balance >= amount) {
                from.balance -= amount;
                to.balance += amount;
                System.out.println("Success! Transferred " + amount + " from " + from.id + " to " + to.id);
            } else {
                System.out.println("Insufficient funds for " + Thread.currentThread().getName());
            }

            to.lock.release();
            from.lock.release();

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

        System.out.println("Starting Deadlock Simulation");
        
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