package com.ibm.demo.day3.concurency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ConcurrencyDemo {

	public static void main(String[] args) throws Exception {

		Callable<Integer> sumTask = () -> {
			int sum = 0;
			for (int i = 1; i <= 10; i++)
				sum += i;
			System.out.println(Thread.currentThread().getName() + " computed: " + sum);
			return sum;
		};

		// Fixed pool with 3 threads
		ExecutorService pool = Executors.newFixedThreadPool(3);

		// Submit multiple tasks, collect futures
		List<Future<Integer>> futures = new ArrayList<>();

		for (int i = 0; i < 5; i++) {
			futures.add(pool.submit(sumTask));
		}

		// Collect all results
		int grandTotal = 0;
		for (Future<Integer> f : futures) {
			grandTotal += f.get(); // blocks per future
		}

		System.out.println("Grand total: " + grandTotal); // 55 × 5 = 275

		// invokeAll — submit all and get all results at once
		List<Future<Integer>> all = pool.invokeAll(List.of(sumTask, sumTask, sumTask));
//		System.out.println(all);
		// invokeAny — return first successful result, cancel others
		Integer first = pool.invokeAny(List.of(sumTask, sumTask, sumTask));
		System.out.println("First result: " + first);

		pool.shutdown();
		pool.awaitTermination(10, TimeUnit.SECONDS);
	}
}