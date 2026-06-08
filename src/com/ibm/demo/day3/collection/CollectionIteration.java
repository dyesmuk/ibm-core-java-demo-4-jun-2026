package com.ibm.demo.day3.collection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CollectionIteration {

	public static void main(String[] args) {

		List<String> friends = new ArrayList<>();

		friends.add("Sonu");
		friends.add("Monu");
		friends.add("Tonu");

//		iterate  - for loop, for each loop, iterator, forEach 

		System.out.println("List of the friends using forEach method ");
//		friends.forEach((friend) -> {
//			System.out.println(friend);
//		});
		
		friends.forEach(friend -> System.out.println(friend));
//		friends.forEach(null);
		
		System.out.println("List of the friends using iterator method ");
		
		Iterator<String> it = friends.iterator();
		
		while (it.hasNext())
			System.out.println(it.next());
		
		
	}

}









