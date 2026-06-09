package com.ibm.demo.day3.features;

public class Java14Features {

	public static void main(String[] args) {

		// switch expression

		int day = 6;

		String result = switch (day) {

		case 6, 7 -> "Weekend";

		default -> "Weekday";
		};

		System.out.println(result);

		// Record (Preview in Java 14)

		Employee e = new Employee(101, "Sonu", 50000);

		System.out.println(e.id());

		System.out.println(e.name());

		System.out.println(e.salary());

		System.out.println(e);

		// instanceof pattern matching

		Object obj = "Java 14";

		if (obj instanceof String s) {

			System.out.println(s.toUpperCase());
		}

		// NullPointerException improvement

		String str = null;

		try {

			System.out.println(str.length());

		} catch (Exception ex) {

			ex.printStackTrace();
		}

		// Helpful JVM info

		System.out.println(Runtime.version());
	}
}

// Record Example

record Employee(int id, String name, double salary) {
}
