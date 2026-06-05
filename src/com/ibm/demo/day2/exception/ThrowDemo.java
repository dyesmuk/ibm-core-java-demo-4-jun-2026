package com.ibm.demo.day2.exception;

public class ThrowDemo {

	public static void main(String[] args) {
		System.out.println("Start");
		try {
			ThrowDemo.checkEligibility(17);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		System.out.println("End");
	}

	static void checkEligibility(int age) {
		if (age >= 18) {
			System.out.println("Eligible");
		} else {
			// code
			throw new NoAgeEligibilityException("Age is < 18!");
		}

	}

}

//package com.ibm.demo.day2.exception;
//
//public class ThrowDemo {
//
//	public static void main(String[] args) {
//		System.out.println("Start");
//		try {
//			ThrowDemo.checkEligibility(17);
//		} catch (RuntimeException e) {
//			e.printStackTrace();
//		}
//		System.out.println("End");
//	}
//
//	static void checkEligibility(int age) {
//		if (age >= 18) {
//			System.out.println("Eligible");
//		} else {
//			// code
//			throw new RuntimeException("Age is < 18!");
//		}
//
//	}
//
//}
