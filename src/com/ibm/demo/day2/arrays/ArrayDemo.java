package com.ibm.demo.day2.arrays;

import java.util.Arrays;

public class ArrayDemo {

	public static void main(String[] args) {

		int[] arr = { 25, 31, 17, 9, 22 };
		System.out.println("Original array");
		for (int a : arr)
			System.out.println(a);
		System.out.println(arr.length);
		Arrays.sort(arr);
		System.out.println("sorted array");
		for (int a : arr)
			System.out.println(a);
		
//		Arrays.

	}

}
