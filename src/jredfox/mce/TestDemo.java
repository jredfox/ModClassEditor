package jredfox.mce;

public class TestDemo {
	
	public static int id = 0;
	
	public static class A
	{
		public A()
		{
			
		}
		
		public A(A child)
		{
			
		}
	}
	
	public static class B extends A
	{
		public static boolean b = true;
		public B()
		{
			super(b ? new A() : new A());
			System.out.println();
		}
	}

}
