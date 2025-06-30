package jredfox.mce;

public class ArrUtils {
	
	public static void fill(boolean[] arr, boolean val, int start, int end) 
	{
		if(arr.length == 0)
			return;//MT ARRAY
		
		if(end == -1)
			end = arr.length - 1;
		
		for(int i=start; i <= end; i++)
			arr[i] = val;
	}
	
	public static void fill(byte[] arr, byte val, int start, int end, int increment) 
	{
		if(arr.length == 0)
			return;//MT ARRAY
		
		if(end == -1)
			end = arr.length - 1;
		
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
			end = arr.length - 1;
		
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
			end = arr.length - 1;
		
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
			end = arr.length - 1;
		
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
			end = arr.length - 1;
		
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
			end = arr.length - 1;
		
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
			end = arr.length - 1;
		
		for(int i=start; i <= end; i++)
			arr[i] = val;
	}
	
	public static void fill(Boolean[] arr, boolean val, int start, int end) 
	{
		if(arr.length == 0)
			return;//MT ARRAY
		
		if(end == -1)
			end = arr.length - 1;
		
		for(int i=start; i <= end; i++)
			arr[i] = val;
	}
	
	public static void fill(Byte[] arr, byte val, int start, int end, int increment) 
	{
		if(arr.length == 0)
			return;//MT ARRAY
		
		if(end == -1)
			end = arr.length - 1;
		
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
			end = arr.length - 1;
		
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
			end = arr.length - 1;
		
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
			end = arr.length - 1;
		
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
			end = arr.length - 1;
		
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
			end = arr.length - 1;
		
		for(int i=start; i <= end; i++)
		{
			arr[i] = val;
			val += increment;
		}
	}
	
	/**
	 * Insert an array into another one replacing it's indexesd
	 */
	public static void insert(boolean[] arr, boolean[] val, int index)
	{
		if(arr.length == 0)
			return;
		if(index == -1)
			index = arr.length - 1;
		
		int valIndex = 0;
	    for (int i = index; i < arr.length && valIndex < val.length; i++) 
	        arr[i] = val[valIndex++];
	}
	
	/**
	 * Insert an array into another one replacing it's indexesd
	 */
	public static void insert(byte[] arr, byte[] val, int index)
	{
		if(arr.length == 0)
			return;
		if(index == -1)
			index = arr.length - 1;
		
		int valIndex = 0;
	    for (int i = index; i < arr.length && valIndex < val.length; i++) 
	        arr[i] = val[valIndex++];
	}
	
	/**
	 * Insert an array into another one replacing it's indexesd
	 */
	public static void insert(short[] arr, short[] val, int index)
	{
		if(arr.length == 0)
			return;
		if(index == -1)
			index = arr.length - 1;
		
		int valIndex = 0;
	    for (int i = index; i < arr.length && valIndex < val.length; i++) 
	        arr[i] = val[valIndex++];
	}
	
	/**
	 * Insert an array into another one replacing it's indexesd
	 */
	public static void insert(int[] arr, int[] val, int index)
	{
		if(arr.length == 0)
			return;
		if(index == -1)
			index = arr.length - 1;
		
		int valIndex = 0;
	    for (int i = index; i < arr.length && valIndex < val.length; i++) 
	        arr[i] = val[valIndex++];
	}
	
	/**
	 * Insert an array into another one replacing it's indexesd
	 */
	public static void insert(long[] arr, long[] val, int index)
	{
		if(arr.length == 0)
			return;
		if(index == -1)
			index = arr.length - 1;
		
		int valIndex = 0;
	    for (int i = index; i < arr.length && valIndex < val.length; i++) 
	        arr[i] = val[valIndex++];
	}
	
	/**
	 * Insert an array into another one replacing it's indexesd
	 */
	public static void insert(float[] arr, float[] val, int index)
	{
		if(arr.length == 0)
			return;
		if(index == -1)
			index = arr.length - 1;
		
		int valIndex = 0;
	    for (int i = index; i < arr.length && valIndex < val.length; i++) 
	        arr[i] = val[valIndex++];
	}
	
	/**
	 * Insert an array into another one replacing it's indexesd
	 */
	public static void insert(double[] arr, double[] val, int index)
	{
		if(arr.length == 0)
			return;
		if(index == -1)
			index = arr.length - 1;
		
		int valIndex = 0;
	    for (int i = index; i < arr.length && valIndex < val.length; i++) 
	        arr[i] = val[valIndex++];
	}
	
	/**
	 * Insert an array into another one replacing it's indexesd
	 */
	public static void insert(String[] arr, String[] val, int index)
	{
		if(arr.length == 0)
			return;
		if(index == -1)
			index = arr.length - 1;
		
		int valIndex = 0;
	    for (int i = index; i < arr.length && valIndex < val.length; i++) 
	        arr[i] = val[valIndex++];
	}
	
	/**
	 * Insert an array into another one replacing it's indexesd
	 */
	public static void insert(Boolean[] arr, boolean[] val, int index)
	{
		if(arr.length == 0)
			return;
		if(index == -1)
			index = arr.length - 1;
		
		int valIndex = 0;
	    for (int i = index; i < arr.length && valIndex < val.length; i++) 
	        arr[i] = val[valIndex++];
	}
	
	/**
	 * Insert an array into another one replacing it's indexesd
	 */
	public static void insert(Byte[] arr, byte[] val, int index)
	{
		if(arr.length == 0)
			return;
		if(index == -1)
			index = arr.length - 1;
		
		int valIndex = 0;
	    for (int i = index; i < arr.length && valIndex < val.length; i++) 
	        arr[i] = val[valIndex++];
	}
	
	/**
	 * Insert an array into another one replacing it's indexesd
	 */
	public static void insert(Short[] arr, short[] val, int index)
	{
		if(arr.length == 0)
			return;
		if(index == -1)
			index = arr.length - 1;
		
		int valIndex = 0;
	    for (int i = index; i < arr.length && valIndex < val.length; i++) 
	        arr[i] = val[valIndex++];
	}
	
	/**
	 * Insert an array into another one replacing it's indexesd
	 */
	public static void insert(Integer[] arr, int[] val, int index)
	{
		if(arr.length == 0)
			return;
		if(index == -1)
			index = arr.length - 1;
		
		int valIndex = 0;
	    for (int i = index; i < arr.length && valIndex < val.length; i++) 
	        arr[i] = val[valIndex++];
	}
	
	/**
	 * Insert an array into another one replacing it's indexesd
	 */
	public static void insert(Long[] arr, long[] val, int index)
	{
		if(arr.length == 0)
			return;
		if(index == -1)
			index = arr.length - 1;
		
		int valIndex = 0;
	    for (int i = index; i < arr.length && valIndex < val.length; i++) 
	        arr[i] = val[valIndex++];
	}
	
	/**
	 * Insert an array into another one replacing it's indexesd
	 */
	public static void insert(Float[] arr, float[] val, int index)
	{
		if(arr.length == 0)
			return;
		if(index == -1)
			index = arr.length - 1;
		
		int valIndex = 0;
	    for (int i = index; i < arr.length && valIndex < val.length; i++) 
	        arr[i] = val[valIndex++];
	}
	
	/**
	 * Insert an array into another one replacing it's indexesd
	 */
	public static void insert(Double[] arr, double[] val, int index)
	{
		if(arr.length == 0)
			return;
		if(index == -1)
			index = arr.length - 1;
		
		int valIndex = 0;
	    for (int i = index; i < arr.length && valIndex < val.length; i++) 
	        arr[i] = val[valIndex++];
	}

}