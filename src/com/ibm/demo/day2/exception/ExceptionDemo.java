package com.ibm.demo.day2.exception;

import java.util.InputMismatchException;
import java.util.Scanner;

public class ExceptionDemo {

	public static void main(String[] args) {

		int num3 = 0;

		try (Scanner sc = new Scanner(System.in);) {
			System.out.println("Enter an integer: ");
			int num = sc.nextInt();
			System.out.println("Enter another one: ");
			int num2 = sc.nextInt();
			num3 = num / num2;
		} catch (InputMismatchException | ArithmeticException e) {
			System.out.println("Wrong!");
		} finally {
			System.out.println(num3);
		}

	}

}

//package com.ibm.demo.day2.exception;
//
//import java.util.InputMismatchException;
//import java.util.Scanner;
//
//public class ExceptionDemo {
//
//	public static void main(String[] args) {
//
//		Scanner sc = new Scanner(System.in);
//		int num3 = 0;
//
//		try {
//			System.out.println("Enter an integer: ");
//			int num = sc.nextInt();
//			System.out.println("Enter another one: ");
//			int num2 = sc.nextInt();
//			num3 = num / num2;
//		} catch (InputMismatchException | ArithmeticException e) {
//			System.out.println("Wrong!");
//		} finally {
//			System.out.println(num3);
//			sc.close();
//		}
//
//	}
//
//}

//package com.ibm.demo.day2.exception;
//
//import java.util.InputMismatchException;
//import java.util.Scanner;
//
//public class ExceptionDemo {
//
//	public static void main(String[] args) {
//
//		Scanner sc = new Scanner(System.in);
//		int num3 = 0;
//
//		try {
//			System.out.println("Enter an integer: ");
//			int num = sc.nextInt();
//			System.out.println("Enter another one: ");
//			int num2 = sc.nextInt();
//			num3 = num / num2;
//		} catch (InputMismatchException | ArithmeticException e) {
//			System.out.println("Wrong!");
//		} finally {
//			System.out.println(num3);
//			sc.close();
//		}
//
//	}
//
//}

//package com.ibm.demo.day2.exception;
//
//import java.util.InputMismatchException;
//import java.util.Scanner;
//
//public class ExceptionDemo {
//
//	public static void main(String[] args) {
//
//		Scanner sc = new Scanner(System.in);
//		int num3 = 0;
//
//		try {
//			System.out.println("Enter an integer: ");
//			int num = sc.nextInt();
//			System.out.println("Enter another one: ");
//			int num2 = sc.nextInt();
//			num3 = num / num2;
//		} catch (InputMismatchException e) {
//			System.out.println("Enter only integers!");
//		} catch (ArithmeticException e) {
//			System.out.println("Do not divide by 0!");
//		} catch (RuntimeException e) {
//			System.out.println("Something is wrong!");
//		} finally {
//			System.out.println(num3);
//			sc.close();
//		}
//
//	}
//
//}

//package com.ibm.demo.day2.exception;
//
//import java.util.Scanner;
//
//public class ExceptionDemo {
//
//	public static void main(String[] args) {
//
//		Scanner sc = new Scanner(System.in);
//		int num3 = 0;
//
//		System.out.println("Enter an integer: ");
//		int num = sc.nextInt();
//
//		System.out.println("Enter another one: ");
//		int num2 = sc.nextInt();
//
//		try {
//			num3 = num / num2;
//		} catch (ArithmeticException e) {
//			System.out.println("Do not divide by 0.");
//		} finally {
//			System.out.println(num3);
//			sc.close();
//		}
//
//	}
//
//}
