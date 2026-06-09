package com.ibm.demo.day3.garbage;

public class GcDemo {

	@Override
	protected void finalize() {
		System.out.println("GC called");
	}

	public static void main(String[] args) {

		GcDemo obj = new GcDemo();
		System.out.println(obj.toString());

		obj = null;

		System.gc();
	}
}