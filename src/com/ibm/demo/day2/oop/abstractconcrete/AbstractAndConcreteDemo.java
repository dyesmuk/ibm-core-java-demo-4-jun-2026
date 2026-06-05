package com.ibm.demo.day2.oop.abstractconcrete;

public class AbstractAndConcreteDemo {
	
	public static void main(String[] args) {
		
		HdfcBank bank1 = new HdfcBank();
		bank1.doKyc();
		bank1.payInterest();
		bank1.checkNationality();
		IciciBank bank2 = new IciciBank();
		bank2.doKyc();
		bank2.payInterest();
		bank2.checkNationality();
		
	}

}
