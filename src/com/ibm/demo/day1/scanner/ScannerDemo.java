package com.ibm.demo.day1.scanner;

import java.util.Scanner;

// take user inputs 

public class ScannerDemo {

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);

		System.out.println("Welcome\nEnter your name:");
		String username = sc.next();
		System.out.println("Welcome " + username + "!");

		sc.close();

	}

}
