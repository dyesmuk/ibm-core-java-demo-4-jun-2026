package com.ibm.demo.day3.collection;

import java.util.ArrayList;
import java.util.List;

public class CollectionTest {

	public static void main(String[] args) {

		List<Integer> nums = new ArrayList<>();

		nums.add(1);
		nums.add(2);
		nums.add(3);
		nums.remove(1);
		System.out.println(nums);
	}

}
