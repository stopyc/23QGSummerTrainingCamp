package com.yinjunbiao;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.FutureTask;

public class Main {
    public static void main(String[] args) {

        for (int i = 0; i < GetTicket.cyclicBarrier.getParties(); i++) {
            GetTicket getTicket = new GetTicket();
            FutureTask<Boolean> booleanFutureTask = new FutureTask<>(getTicket);
            new Thread(booleanFutureTask).start();
        }

    }
}