package com.ibm.demo.day3.inner;

// use abstract method from an interface 

public class InnerDemo {

	public static void main(String[] args) {

		// Option 1 - use concrete class
		Calc calc = new CalcMethods();
		calc.addNums(10, 20);

		// Option 2 - use annon inner class
		Calc calc2 = new Calc() {
			@Override
			public int addNums(int i, int j) {
				return i + j;
			}

			@Override
			public int subNums(int i, int j) {
				return i - j;
			}
		};
		calc2.addNums(10, 20);
		calc2.subNums(10, 5);
		
//		Option 3 - use lambda - 
		
//		Calc calc3 = ;
//		calc3.addNums(10, 20);
		
		
	}

}


//package com.ibm.demo.day3.inner;
//
//// use abstract method from an interface 
//
//public class InnerDemo {
//
//	public static void main(String[] args) {
//
//		class LocalClass {
//
//		}
//
//		// Option 1 - use concrete class
//		Calc calc = new CalcMethods();
//		calc.addNums(10, 20);
//
//		// Option 2 - use annon inner class
//
//		Calc calc2 = new Calc() {
//			@Override
//			public int addNums(int i, int j) {
//				return i + j;
//			}
//
//			@Override
//			public int subNums(int i, int j) {
//				return i - j;
//			}
//		};
//
//		calc2.addNums(10, 20);
//		calc2.subNums(10, 5);
//
//	}
//
//	class InstanceClass {
//
//	}
//
//	static class StaticClass {
//
//	}
//
//}
