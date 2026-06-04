package com.ibm.demo.day1.constructor;

public class ConstructorDemo {

	public static void main(String[] args) {

		Employee emp1 = new Employee();
		emp1.id = 1;
		emp1.name = "Sonu";
		emp1.salary = 10.75;
		System.out.println(emp1.toString());

		Employee emp2 = new Employee();
		emp2.id = 2;
		emp2.name = "Monu";
		emp2.salary = 11.25;
		System.out.println(emp2.toString());

		Employee emp3 = new Employee();
		System.out.println(emp3.toString());
	}

}
