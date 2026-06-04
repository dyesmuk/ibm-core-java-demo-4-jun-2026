package com.ibm.demo.day1.constructor;

public class Employee {

	int id;
	String name;
	double salary;

	public Employee() {
		super();
		System.out.println("Default constructor");
	}

	public Employee(int id, String name) {
		super();
		System.out.println("2 args constructor");
		this.id = id;
		this.name = name;
	}

	public Employee(int id, String name, double salary) {
		super();
		System.out.println("All args constructor");
		this.id = id;
		this.name = name;
		this.salary = salary;
	}

	@Override
	public String toString() {
		return "Employee [id=" + id + ", name=" + name + ", salary=" + salary + "]";
	}

}
