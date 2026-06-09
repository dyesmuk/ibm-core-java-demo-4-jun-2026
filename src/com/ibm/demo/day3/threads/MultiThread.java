package com.ibm.demo.day3.threads;

public class MultiThread extends Thread {

	public int num = 1;

	@Override
	public void run() {
		printNums();
	}

	public synchronized void printNums() {
		for (int i = 1; i <= 10; i++) {
			num++;
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.print(i);
		}
		System.out.println("num: " + num);
	}
}
