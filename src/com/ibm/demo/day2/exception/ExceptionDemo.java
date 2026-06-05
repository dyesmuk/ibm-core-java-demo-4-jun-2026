package com.ibm.demo.day2.exception;

import java.util.Scanner;

public class ExceptionDemo {

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);

		System.out.println("Enter an integer: ");
		int num = sc.nextInt();
		System.out.println("Enter another one: ");
		int num2 = sc.nextInt();

		int num3 = num / num2;

		System.out.println(num3);

		sc.close();

	}

}
