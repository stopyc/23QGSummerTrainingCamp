import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class TicketMachine {
    final int threadQuantity;
    final int ticketsQuantity;
    private ThreadFactory factory;
    private AtomicInteger tickets;
    private CyclicBarrier barrier;


    public int getThreadQuantity() {
        return threadQuantity;
    }

    public int getTicketsQuantity() {
        return ticketsQuantity;
    }


    public void startGrabTicket() {
        for (int i = 0; i < threadQuantity; i++) {
            Thread thread = factory.newThread(() -> {
                try {
                    barrier.await();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                boolean ticketFlag = false;
                while (!ticketFlag && tickets.get() > 0) {
                    System.out.println(Thread.currentThread().getName() + "\t抢到了\t第" + (ticketsQuantity - tickets.decrementAndGet()) + "张票");
                    ticketFlag = true;
                }
            });
            thread.start();
        }
    }
    public TicketMachine(int threadQuantity, int ticketsQuantity) {
        this.threadQuantity = threadQuantity;
        this.ticketsQuantity = ticketsQuantity;
        tickets = new AtomicInteger(ticketsQuantity);

         factory = new ThreadFactory() {
            private AtomicInteger threadCount = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("线程" + threadCount.getAndIncrement());
                return thread;
            }
        };

        barrier = new CyclicBarrier(threadQuantity, () -> {
            System.out.println(threadQuantity + "个线程即将开始抢" + ticketsQuantity + "张票");
        });
    }
}
