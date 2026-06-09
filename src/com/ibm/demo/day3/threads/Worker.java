package com.ibm.demo.day3.threads;

class Worker implements Runnable {

	@Override
	public void run() {

		for (int i = 1; i <= 3; i++) {

			System.out.println(Thread.currentThread().getName() + " working...");
		}
	}
}
