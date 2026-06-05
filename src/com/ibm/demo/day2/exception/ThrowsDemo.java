
package com.ibm.demo.day2.exception;

public class ThrowsDemo {

	public static void main(String[] args) {
		System.out.println("Start");
		try {
			ThrowsDemo.printNums();
		} catch (InterruptedException e) {
			e.printStackTrace(); // custom handling 
		}
		System.out.println("End");
	}

	public static void printNums() throws InterruptedException {
		for (int i = 1; i <= 10; i++) {
			Thread.sleep(250);
			System.out.println(i);
		}
	}
}

//package com.ibm.demo.day2.exception;
//
//public class ThrowsDemo {
//
//	public static void main(String[] args) {
//		System.out.println("Start");
//		ThrowsDemo.printNums();
//		System.out.println("End");
//	}
//
//	public static void printNums() {
//		for (int i = 1; i <= 10; i++) {
//			try {
//				Thread.sleep(250);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			System.out.println(i);
//		}
//	}
//}
