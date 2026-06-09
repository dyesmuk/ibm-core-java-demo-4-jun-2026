package com.ibm.demo.day3.iosdemo;

import java.io.FileReader;

public class IoDemo {

	public static void main(String[] args) {
		System.out.println("Start");

		try {
			FileReader reader = new FileReader("sample.txt");
			int ch;

			while ((ch = reader.read()) != -1) {

				System.out.print((char) ch);
			}

			reader.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("End");

	}
}