package com.ibm.demo.day3.collection;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CollectionMethods {

	public static void main(String[] args) {

		ArrayList<String> friends = new ArrayList<>();
		
		List<String> friends2 = new ArrayList<>();
		int num = 10;
		
//		friends =  new LinkedList<String>();
		friends2 = new LinkedList<>();

		friends.add("Sonu");
		friends.add("Monu");
		friends.add("Tonu");

		System.out.println(friends);

		@SuppressWarnings("unchecked")
		ArrayList<String> friends3 = (ArrayList<String>) friends.clone();
//		ArrayList<String> friends3 =  new ArrayList<>(friends);

		System.out.println(friends3);

		friends3.add("Ponu");

		System.out.println(friends);
		System.out.println(friends2);
		System.out.println(friends3);
//		friends.

//		 friends2.clone(); // CE 
	}
}
//package com.ibm.demo.day3.collection;
//
//import java.util.ArrayList;
//import java.util.LinkedList;
//import java.util.List;
//
//public class CollectionMethods {
//
//	public static void main(String[] args) {
//
//		ArrayList<String> friends = new ArrayList<>(); // 
//		List<String> friends2 = new ArrayList<>();
//		System.out.println(friends);
//		System.out.println(friends2);
//		
////		friends = new LinkedList(); // Type mismatch
////		friends2 = new LinkedList<>(); // Works 
//		
//		ArrayList<String> friends3 =  friends.clone();
//
////		System.out.println(friends.size());
////		System.out.println(friends);
////		friends.add("Sonu");
////		friends.add("Monu");
////		friends.add("Tonu");
////		System.out.println(friends.size());
////		System.out.println(friends);
////		System.out.println(friends.size());
////		System.out.println(friends);
//////		friends.
////		System.out.println(friends2);
//
//	}
//
//}
