package threadTest.Practice;
  
import java.util.LinkedList;
  
public class People {
  
    // 线程池大小
    int threadPoolSize;
  
    // 使用LinkedList来存储购票任务。
    LinkedList<Runnable> tickets = new LinkedList<Runnable>();
  
    // 试图购票的线程
  
    public People() {
        threadPoolSize = 100;
  
        // 启动100个购票消费者线程
        synchronized (tickets) {
            for (int i = 0; i < threadPoolSize; i++) {
                new TaskConsumeThread("购票消费者线程 " + i).start();
            }
        }
    }
  
    public void add(Runnable r) {
        synchronized (tickets) {
            tickets.add(r);
            // 唤醒等待的购票消费者线程
            tickets.notifyAll();
        }
    }
  
    class TaskConsumeThread extends Thread {
        public TaskConsumeThread(String name) {
            super(name);
        }
  
        Runnable task;
  
        public void run() {
            System.out.println("启动： " + this.getName());
            while (true) {
                synchronized (tickets) {
                    while (tickets.isEmpty()) {
                        try {
                            tickets.wait();
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    task = tickets.removeLast();
                    // 允许添加任务的线程可以继续添加任务
                    // tickets.notifyAll();
  
                }

                task.run();
                

                System.out.println(this.getName() + "获取到票");
                try {
                    Thread.sleep(100000000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
    }
}
}