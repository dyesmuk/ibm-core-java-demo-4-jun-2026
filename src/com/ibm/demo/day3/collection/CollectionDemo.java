package com.ibm.demo.day3.collection;

import java.util.ArrayList;

public class CollectionDemo {

	public static void main(String[] args) {

//		String[] str = { "Sonu", "Monu", "Tonu" };

		ArrayList friends = new ArrayList();
		System.out.println(friends.size());
		System.out.println(friends);
		friends.add("Sonu");
		friends.add("Monu");
		friends.add("Tonu");
		System.out.println(friends.size());
		System.out.println(friends);
		friends.remove(2);
		System.out.println(friends.size());
		System.out.println(friends);
		friends.remove("Zonu");
		System.out.println(friends.size());
		System.out.println(friends);

	}

}

//package com.ibm.demo.day3.collection;
//
//public class CollectionDemo {
//
//	public static void main(String[] args) {
//
//		String[] str = { "Sonu", "Monu", "Tonu" };
//
//		for (String s : str)
//			System.out.println(s);
//		
////		str[3] = "Ponu"; // CE 
//		str[2] = "Ponu";
//		
//		for (String s : str)
//			System.out.println(s);
//		
//
//	}
//
//}
