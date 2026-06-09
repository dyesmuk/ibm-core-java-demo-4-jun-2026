package com.ibm.demo.day3.concurency;

import java.util.concurrent.*;

public class CallableDemo {

    public static void main(String[] args)
            throws Exception {

        ExecutorService service =
                Executors.newSingleThreadExecutor();

        Callable<Integer> task = () -> {

            return 100;
        };

        Future<Integer> future =
                service.submit(task);

        System.out.println(future.get());

        service.shutdown();
    }
}