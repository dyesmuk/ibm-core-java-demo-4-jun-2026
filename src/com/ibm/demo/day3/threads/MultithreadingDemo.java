package com.ibm.demo.day3.threads;

public class MultithreadingDemo {

	public static void main(String[] args) {

		MultiThread obj = new MultiThread();
		obj.start();
		MultiThread obj2 = new MultiThread();
		obj2.start();
		MultiThread obj3 = new MultiThread();
		obj3.start();
	}
}
