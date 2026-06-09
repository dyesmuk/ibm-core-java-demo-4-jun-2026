package com.ibm.demo.day3.threads;

class Worker implements Runnable {
//class Worker extends Thread {

	@Override
	public void run() {
		method();
	}

	public void method() {

		for (int i = 1; i <= 3; i++) {

			System.out.println(Thread.currentThread().getName() + " working...");
		}
	}
}
