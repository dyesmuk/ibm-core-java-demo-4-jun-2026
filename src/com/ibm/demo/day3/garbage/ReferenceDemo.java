package com.ibm.demo.day3.garbage;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

public class ReferenceDemo {

	public static void main(String[] args) {
		
		// 1. strong reference
		String name = new String("Hello"); // strong reference
		// name is NOT collected as long as this variable is in scope
		System.out.println(name);
		name = null; // now eligible for GC
		System.gc();
		System.out.println("Object may be collected now");

		// 2. soft reference

		SoftReference<byte[]> cache = new SoftReference<>(new byte[1024]);
		byte[] data = cache.get(); // returns object if still alive
		if (data != null) {
			System.out.println("Cache hit: " + data.length + " bytes");
		} else {
			System.out.println("Cache miss - GC cleared it (low memory)");
		}
		
		// 3. weak reference
		
		 String data2 = new String("temporary");
	        WeakReference<String> weak = new WeakReference<>(data2);

	        System.out.println("Before GC: " + weak.get());  // "temporary"

	        data2 = null;     // remove strong reference
	        System.gc();     // hint to run GC

	        System.out.println("After GC: " + weak.get());   // null (collected)
		
		
		// 4. phantom reference


		
	}
}