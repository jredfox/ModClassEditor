package jredfox.mce;

public class TestDemo {
	
	public static int id = 0;
	
	public static class A
	{
		public A(Object a)
		{
			this(new A[]{new A()});
		}
		
		public A()
		{
			this(new A[][] {{new A()}, {new A()} }); if(id == 0){System.out.println();}
		}
		
		public A(A[][] a)
		{
			
		}
		
		public A(A[] children)
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
			super(b ? new A() :
				new A());
			System.out.println(new A());
		}
	}

}
