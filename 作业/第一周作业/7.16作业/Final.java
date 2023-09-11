package Thread.task2;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
public class Final {
    private static final int TOTAL_TICKETS = 10;
    private static final int TOTAL_USERS = 100;
    //    private static int remainTickets = TOTAL_TICKETS;
    private static ReentrantLock lock = new ReentrantLock();
    private static CyclicBarrier barrier = new CyclicBarrier(TOTAL_USERS);
    public static void main(String[] args) {
        AtomicInteger remainTickets;
        remainTickets = new AtomicInteger(TOTAL_TICKETS);
        for (int i = 0; i < TOTAL_USERS; i++) {
            int userId = i + 1;
            new Thread(() -> {
                try {
                    barrier.await(); // 等待所有线程就绪
                    bookTicket(userId, remainTickets);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, "User " + userId).start();
        }
    }
    private static void bookTicket(int userId, AtomicInteger remainTickets) {
        lock.lock(); // 获取锁，保证同一时间只有一个线程能修改票数
        try {
            if (remainTickets.get() > 0) {
                System.out.println(Thread.currentThread().getName() + "抢到了序号为\t" + remainTickets.get() + "的票");
                remainTickets.decrementAndGet();
            } else {

            }
        } finally {
            lock.unlock(); // 释放锁
        }
    }
}