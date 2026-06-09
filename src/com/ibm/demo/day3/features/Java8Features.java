package com.ibm.demo.day3.features;

import java.util.List;
import java.util.Optional;

public class Java8Features {

//	Lambda Expressions
//	Anonymous functions — write inline code without boilerplate anonymous classes.

	// Before
	Runnable r1=new Runnable(){public void run(){System.out.println("Hi");}};

	// After
	Runnable r2 = () -> System.out.println("Hi");
//	Stream API
//	Functional-style operations on collections — filter, map, reduce, etc.

	List<String> names1 = List.of("Alice", "Bob", "Anna");
	
	names.stream().filter(n->n.startsWith("A")).forEach(System.out::println); // Alice, Anna
	
//	Optional<T>
//	A container that may or may not hold a value — avoids NullPointerException.

	Optional<String> name = Optional.of("Alice");name.ifPresent(System.out::println); // Alice
	
	String val = name.orElse("Unknown"); // "Alice"

	//	Default Methods
//	Interfaces can
//	now have
//	method implementations
//	using the default keyword.

	interface Greeter {
		default void greet() {
			System.out.println("Hello!");
		}
	}

	class MyGreeter implements Greeter {
	} // inherits greet()

//	New Date/
//	Time API Immutable,thread-safe date/
//	time classes
//	replacing the
//	old Date/Calendar.

	LocalDate today = LocalDate.now(); // 2026-06-08
	LocalDate birthday = LocalDate.of(1990, 5, 15);
	Period age = Period.between(birthday, today);System.out.println(age.getYears()); // 36
	Method References Shorthand for
	lambdas that
	just call
	an existing
	method.

			List<String> names = List.of("Bob", "Alice");names.forEach(System.out::println); // :: references a method

}
