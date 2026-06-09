package com.ibm.demo.day3.threads;


public class ThreadDemo {

	public static void main(String[] args) {

		Thread t1 = new Thread(new Worker(), "Sonu");
		Thread t2 = new Thread(new Worker(), "Monu");
		Thread t3 = new Thread(new Worker(), "Tonu");

		t1.start();
		t2.start();
		t3.start();
	}
}