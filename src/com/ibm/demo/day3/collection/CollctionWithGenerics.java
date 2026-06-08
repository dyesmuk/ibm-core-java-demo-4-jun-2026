package com.ibm.demo.day3.collection;

import java.util.ArrayList;

public class CollctionWithGenerics { 

	public static void main(String[] args) {

//		ArrayList<String> friends = new ArrayList<String>();
		ArrayList<String> friends = new ArrayList<>();

		System.out.println(friends.size());
		System.out.println(friends);
		friends.add("Sonu");
		friends.add("Monu");
		friends.add("Tonu");
		System.out.println(friends.size());
		System.out.println(friends);
//		friends.add(10.25);
//		friends.add(false);
//		friends.add(null);
		System.out.println(friends.size());
		System.out.println(friends);

	}

}
