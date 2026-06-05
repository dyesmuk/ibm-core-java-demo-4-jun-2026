package com.ibm.demo.day2.oop.abstractconcrete;

// abstract class 
public abstract  class AbsDemo {

//	concrete method - what to do and how to do both 

	// method signature - what does this method do ?
	public void doThis() 
	// method body - how does it do it ?
	{
		System.out.println("doing");
	}

//	 abstract method only what to do
	public abstract void doThisToo();
}
