package com.ibm.demo.day1.methods;

public class MethodDemo {

	// instance method == objectName.methodName();
	void printNums() {
		for (int i = 1; i <= 5; i++)
			System.out.println(i);
	}

	// static method == ClassName.methodName();
	static void printNums2() {
		for (int i = 1; i <= 5; i++)
			System.out.println(i);
	}

	public static void main(String[] args) {

		MethodDemo obj = new MethodDemo();
		obj.printNums(); // works
		MethodDemo.printNums2(); // works
//		MethodDemo.printNums(); // CE 
//		obj.printNums2(); // warning 

//		printNums2();

	}

}
