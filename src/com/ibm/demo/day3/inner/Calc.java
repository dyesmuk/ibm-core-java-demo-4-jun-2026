package com.ibm.demo.day3.inner;

@FunctionalInterface
interface Tax {

	public abstract double gst(double amount);
}

@FunctionalInterface
public interface Calc {

	public abstract int addNums(int i, int j);

//	public abstract int subNums(int i, int j);

}

class CalcMethods implements Calc {

	@Override
	public int addNums(int i, int j) {
		return i + j;
	}

//	@Override
//	public int subNums(int i, int j) {
//		return i - j;
//	}

}
