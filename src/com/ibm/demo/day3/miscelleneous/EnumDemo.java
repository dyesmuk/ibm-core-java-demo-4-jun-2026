package com.ibm.demo.day3.miscelleneous;

public class EnumDemo {

	public static void main(String[] args) {

//		String today = "Monday";
//		today = "Friday";
//		today = "Dryday";

		DayOfWeek today = DayOfWeek.MONDAY;
		System.out.println(today);
		today = DayOfWeek.FRIDAY;
		System.out.println(today);
//		today = "Dryday"; // CE 

	}

}

//package com.ibm.demo.day3.miscelleneous;
//
//import com.ibm.demo.day1.object.Employee;
//
//// final and enum 
//
//public class EnumDemo {
//	
////	private final static int NUM = 0;
////	private final static Employee EMPLOYEE = new Employee();
//	
//	public static void main(String[] args) {
//		
//		
//	}
//
//}
