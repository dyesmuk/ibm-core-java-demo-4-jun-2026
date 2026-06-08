package com.ibm.demo.day3.iosdemo;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class BufferedWriterDemo {

	public static void main(String[] args) {
		
		String file = "sample2.txt";

		try {

			BufferedWriter bw = new BufferedWriter(new FileWriter(file));

			bw.write("Sonu");

			bw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}