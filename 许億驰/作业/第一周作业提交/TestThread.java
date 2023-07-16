package threadTest.Practice;

import threadTest.Practice.People.TaskConsumeThread;

public class TestThread {
       
    public static void main(String[] args) {
        People people = new People();
  
        for (int i = 0; i < 10; i++) {
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    
                }
                
            };
            System.out.println(i);
             
            people.add(task);
             
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }
    }
}