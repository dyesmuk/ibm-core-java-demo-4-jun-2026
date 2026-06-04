package com.ibm.demo.day1.modifiers.package2;

import com.ibm.demo.day1.modifiers.package1.SpecifierDemo;

public class OutsidePackage {
	
	public static void main(String[] args) {

		System.out.println(SpecifierDemo.num1);
//		System.out.println(SpecifierDemo.num2);
//		System.out.println(SpecifierDemo.num3);
//		System.out.println(SpecifierDemo.num4); // CE

	}

}
