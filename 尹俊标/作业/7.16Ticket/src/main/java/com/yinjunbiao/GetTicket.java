package com.yinjunbiao;

import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;

public class GetTicket implements Callable<Boolean> {

    private  boolean isGetTicket = false;
    static CyclicBarrier cyclicBarrier = new CyclicBarrier(100);


    @Override
    public Boolean call() throws Exception {
        if (!isGetTicket){
            cyclicBarrier.await();
            isGetTicket = Ticket.getTicket();
            return isGetTicket;
        }else {
            return false;
        }
    }
}
