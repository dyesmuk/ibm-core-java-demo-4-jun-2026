package com.ibm.demo.day3.consurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrencyDemo {

    public static void main(String[] args) {

        ExecutorService service =
                Executors.newFixedThreadPool(3);

        for (int i = 1; i <= 5; i++) {

            int taskId = i;

            service.execute(() -> {

                System.out.println(
                        "Task " + taskId
                                + " : "
                                + Thread.currentThread().getName()
                );
            });
        }

        service.shutdown();
    }
}