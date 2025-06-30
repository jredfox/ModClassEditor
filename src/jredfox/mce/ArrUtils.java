package jredfox.mce;

public class ArrUtils {
	
	public static void fill(boolean[] arr, boolean val, int start, int end) 
	{
		if(arr.length == 0)
			return;//MT ARRAY
		
		if(end == -1)
			end = arr.length -1;
		
		for(int i=start; i <= end; i++)
			arr[i] = val;
	}
	
	public static void fill(byte[] arr, byte val, int start, int end, int increment) 
	{
		if(arr.length == 0)
			return;//MT ARRAY
		
		if(end == -1)
			end = arr.length -1;
		
		for(int i=start; i <= end; i++)
		{
			arr[i] = val;
			val += increment;
		}
	}
	
	public static void fill(short[] arr, short val, int start, int end, int increment) 
	{
		if(arr.length == 0)
			return;//MT ARRAY
		
		if(end == -1)
			end = arr.length -1;
		
		for(int i=start; i <= end; i++)
		{
			arr[i] = val;
			val += increment;
		}
	}
	
	public static void fill(int[] arr, int val, int start, int end, int increment) 
	{
		if(arr.length == 0)
			return;//MT ARRAY
		
		if(end == -1)
			end = arr.length -1;
		
		for(int i=start; i <= end; i++)
		{
			arr[i] = val;
			val += increment;
		}
	}
	
	public static void fill(long[] arr, long val, int start, int end, int increment) 
	{
		if(arr.length == 0)
			return;//MT ARRAY
		
		if(end == -1)
			end = arr.length -1;
		
		for(int i=start; i <= end; i++)
		{
			arr[i] = val;
			val += increment;
		}
	}
	
	public static void fill(float[] arr, float val, int start, int end, int increment) 
	{
		if(arr.length == 0)
			return;//MT ARRAY
		
		if(end == -1)
			end = arr.length -1;
		
		for(int i=start; i <= end; i++)
		{
			arr[i] = val;
			val += increment;
		}
	}
	
	public static void fill(double[] arr, double val, int start, int end, int increment) 
	{
		if(arr.length == 0)
			return;//MT ARRAY
		
		if(end == -1)
			end = arr.length -1;
		
		for(int i=start; i <= end; i++)
		{
			arr[i] = val;
			val += increment;
		}
	}
	
	public static void fill(String[] arr, String val, int start, int end) 
	{
		if(arr.length == 0)
			return;//MT ARRAY
		
		if(end == -1)
			end = arr.length -1;
		
		for(int i=start; i <= end; i++)
			arr[i] = val;
	}
	
	public static void fill(Boolean[] arr, boolean val, int start, int end) 
	{
		if(arr.length == 0)
			return;//MT ARRAY
		
		if(end == -1)
			end = arr.length -1;
		
		for(int i=start; i <= end; i++)
			arr[i] = val;
	}
	
	public static void fill(Byte[] arr, byte val, int start, int end, int increment) 
	{
		if(arr.length == 0)
			return;//MT ARRAY
		
		if(end == -1)
			end = arr.length -1;
		
		for(int i=start; i <= end; i++)
		{
			arr[i] = val;
			val += increment;
		}
	}
	
	public static void fill(Short[] arr, short val, int start, int end, int increment) 
	{
		if(arr.length == 0)
			return;//MT ARRAY
		
		if(end == -1)
			end = arr.length -1;
		
		for(int i=start; i <= end; i++)
		{
			arr[i] = val;
			val += increment;
		}
	}
	
	public static void fill(Integer[] arr, int val, int start, int end, int increment) 
	{
		if(arr.length == 0)
			return;//MT ARRAY
		
		if(end == -1)
			end = arr.length -1;
		
		for(int i=start; i <= end; i++)
		{
			arr[i] = val;
			val += increment;
		}
	}
	
	public static void fill(Long[] arr, long val, int start, int end, int increment) 
	{
		if(arr.length == 0)
			return;//MT ARRAY
		
		if(end == -1)
			end = arr.length -1;
		
		for(int i=start; i <= end; i++)
		{
			arr[i] = val;
			val += increment;
		}
	}
	
	public static void fill(Float[] arr, float val, int start, int end, int increment) 
	{
		if(arr.length == 0)
			return;//MT ARRAY
		
		if(end == -1)
			end = arr.length -1;
		
		for(int i=start; i <= end; i++)
		{
			arr[i] = val;
			val += increment;
		}
	}
	
	public static void fill(Double[] arr, double val, int start, int end, int increment) 
	{
		if(arr.length == 0)
			return;//MT ARRAY
		
		if(end == -1)
			end = arr.length -1;
		
		for(int i=start; i <= end; i++)
		{
			arr[i] = val;
			val += increment;
		}
	}

}
