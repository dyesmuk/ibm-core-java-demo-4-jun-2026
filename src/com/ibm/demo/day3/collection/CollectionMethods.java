package com.ibm.demo.day3.collection;

import java.util.ArrayList;

public class CollectionMethods {

	public static void main(String[] args) {

		ArrayList<String> friends = new ArrayList<>();

		System.out.println(friends.size());
		System.out.println(friends);
		friends.add("Sonu");
		friends.add("Monu");
		friends.add("Tonu");
		System.out.println(friends.size());
		System.out.println(friends);
		System.out.println(friends.size());
		System.out.println(friends);

	}

}
