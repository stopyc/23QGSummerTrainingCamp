package com.yinjunbiao;

import java.util.concurrent.atomic.AtomicInteger;

public class Ticket {
    private static AtomicInteger ticket;

    private static boolean isGetTicket = false;

    private static Integer ticketNumber = 10;
    static {
        ticket = new AtomicInteger(ticketNumber);
    }

    public static boolean getTicket(){
        if (ticket.get() > 0){
            synchronized (ticket){ 
                if (ticket.get() > 0){
                    ticket.decrementAndGet();
                    System.out.println(Thread.currentThread().getName() + "成功抢到第" + (ticketNumber -ticket.get()) + "张票");
                    return true;
                }
            }
        }
        return false;
    }


}
