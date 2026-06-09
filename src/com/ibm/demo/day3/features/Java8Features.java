package com.ibm.demo.day3.features;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Java8Features {

	public static void main(String[] args) {

		// Lambda Expression

		Runnable r = () -> System.out.println("Hi from Lambda");

		r.run();

		// Stream API

		List<String> names = List.of("Sonu", "Monu", "Tonu", "Ponu");

		names.stream().filter(n -> n.startsWith("A")).forEach(System.out::println);

		// map()

		List<String> upper = names.stream().map(String::toUpperCase).collect(Collectors.toList());

		System.out.println(upper);

		// sorted()

		names.stream().sorted().forEach(System.out::println);

		// count()

		long count = names.stream().filter(n -> n.startsWith("A")).count();

		System.out.println(count);

		// Optional

		Optional<String> name = Optional.of("Sonu");

		name.ifPresent(System.out::println);

		System.out.println(name.orElse("Unknown"));

		// forEach + Method Reference

		names.forEach(System.out::println);

		// Date Time API

		LocalDate today = LocalDate.now();

		LocalDate birthday = LocalDate.of(1990, 5, 15);

		Period age = Period.between(birthday, today);

		System.out.println(age.getYears());

		// Default Method

		MyGreeter g = new MyGreeter();

		g.greet();

		// Functional Interface

		Calculator c = (a, b) -> a + b;

		System.out.println(c.add(10, 20));

		// Predicate

		List<Integer> nums = List.of(10, 15, 20, 25);

		nums.stream().filter(n -> n % 2 == 0).forEach(System.out::println);

		// Consumer

		names.forEach(n -> System.out.println("Hello " + n));

		// Supplier

		Supplier<String> s = () -> "Java 8";

		System.out.println(s.get());

		// Binary Operator

		BinaryOperator<Integer> bo = (a, b) -> a * b;

		System.out.println(bo.apply(5, 6));
	}
}

// Default Method Example

interface Greeter {

	default void greet() {

		System.out.println("Hello!");
	}
}

class MyGreeter implements Greeter {

}

// Functional Interface Example

@FunctionalInterface
interface Calculator {

	int add(int a, int b);
}
