package com.ibm.demo.day3.threads;

public class MultiThread extends Thread {

	@Override
	public void run() {
		printNums();
	}

	public void printNums() {

		for (int i = 1; i <= 10; i++) {
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(i);
		}
	}
}
