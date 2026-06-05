package com.ibm.demo.day2.oop.inheritance;

public class Phone {

}

class BasicPhone {

	public void call() {
		System.out.println("calling...");
	}

	public void sms() {
		System.out.println("texting...");
	}
}

class FeaturePhone extends BasicPhone {

	public void music() {
		System.out.println("playing...");
	}

}

class SmartPhone extends FeaturePhone {

	@Override
	public void music() {
		System.out.println("playing dolby...");
	}

}


