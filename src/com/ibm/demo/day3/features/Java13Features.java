package com.ibm.demo.day3.features;

public class Java13Features {

	public static void main(String[] args) {

		// Text Blocks

		String html = """
				<html>
				    <body>
				        <h1>Java 13</h1>
				    </body>
				</html>
				""";

		System.out.println(html);

		// switch expression

		String day = "MONDAY";

		String type = switch (day) {

		case "SATURDAY", "SUNDAY" -> "Weekend";

		default -> "Weekday";
		};

		System.out.println(type);

		// yield in switch

		int num = 2;

		String result = switch (num) {

		case 1:
			yield "One";

		case 2:
			yield "Two";

		default:
			yield "Unknown";
		};

		System.out.println(result);

		// String methods

		String name = "   Java 13   ";

		System.out.println(name.strip());

		System.out.println("".isBlank());

		System.out.println("Java\nPython".lines().count());

		// File Read/Write (NIO improvements)

		try {

			java.nio.file.Path path = java.nio.file.Path.of("demo.txt");

			java.nio.file.Files.writeString(path, "Hello Java 13");

			String data = java.nio.file.Files.readString(path);

			System.out.println(data);

		} catch (Exception e) {

			e.printStackTrace();
		}
	}
}
