package com.ibm.demo;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ibm.demo.model.Employee;

public class SpringDemo {

	public static void main(String[] args) {
		System.out.println("Start");
//		Employee emp = new Employee(1, "Sonu", 10.50);
//		System.out.println(emp.toString());

		ApplicationContext context = new ClassPathXmlApplicationContext("SpringConfig.xml");
		Employee emp = context.getBean(Employee.class);
		System.out.println(emp.toString());

		((AbstractApplicationContext) context).close();
		System.out.println("End");

	}

}
