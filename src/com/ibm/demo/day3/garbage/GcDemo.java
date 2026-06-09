package com.ibm.demo.day3.garbage;

public class GcDemo {

	@Override
	protected void finalize() {

		System.out.println("GC called");
	}

	public static void main(String[] args) {

		GcDemo obj = new GcDemo();

		obj = null;

		System.gc();
	}
}