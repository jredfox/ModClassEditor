package jredfox.mce;

import jredfox.mce.ArrUtils.Type;

public class ArrUtils {
	
	public static enum Type
	{
		BOOLEAN,
		BYTE,
		SHORT,
		INT,
		LONG,
		FLOAT,
		DOUBLE,
		STRING,
		WRAPPED_BOOLEAN,
		WRAPPED_BYTE,
		WRAPPED_SHORT,
		WRAPPED_INT,
		WRAPPED_LONG,
		WRAPPED_FLOAT,
		WRAPPED_DOUBLE,
		NULL
	}
	
	public static void set(boolean[] arr, int index, boolean val)
	{
		if(index == -1)
			index = arr.length - 1;
		arr[index] = val;
	}
	
	public static void set(byte[] arr, int index, byte val)
	{
		if(index == -1)
			index = arr.length - 1;
		arr[index] = val;
	}
	
	public static void set(short[] arr, int index, short val)
	{
		if(index == -1)
			index = arr.length - 1;
		arr[index] = val;
	}
	
	public static void set(int[] arr, int index, int val)
	{
		if(index == -1)
			index = arr.length - 1;
		arr[index] = val;
	}
	
	public static void set(long[] arr, int index, long val)
	{
		if(index == -1)
			index = arr.length - 1;
		arr[index] = val;
	}
	
	public static void set(float[] arr, int index, float val)
	{
		if(index == -1)
			index = arr.length - 1;
		arr[index] = val;
	}
	
	public static void set(double[] arr, int index, double val)
	{
		if(index == -1)
			index = arr.length - 1;
		arr[index] = val;
	}
	
	public static void set(String[] arr, int index, String val)
	{
		if(index == -1)
			index = arr.length - 1;
		arr[index] = val;
	}
	
	public static void set(Boolean[] arr, int index, boolean val)
	{
		if(index == -1)
			index = arr.length - 1;
		arr[index] = val;
	}
	
	public static void set(Byte[] arr, int index, byte val)
	{
		if(index == -1)
			index = arr.length - 1;
		arr[index] = val;
	}
	
	public static void set(Short[] arr, int index, short val)
	{
		if(index == -1)
			index = arr.length - 1;
		arr[index] = val;
	}
	
	public static void set(Integer[] arr, int index, int val)
	{
		if(index == -1)
			index = arr.length - 1;
		arr[index] = val;
	}
	
	public static void set(Long[] arr, int index, long val)
	{
		if(index == -1)
			index = arr.length - 1;
		arr[index] = val;
	}
	
	public static void set(Float[] arr, int index, float val)
	{
		if(index == -1)
			index = arr.length - 1;
		arr[index] = val;
	}
	
	public static void set(Double[] arr, int index, double val)
	{
		if(index == -1)
			index = arr.length - 1;
		arr[index] = val;
	}
	
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

	public static Type getType(String type) 
	{
		boolean isWrapper = Character.isUpperCase(type.charAt(0));
		type = type.toLowerCase();
		if(type.equals("boolean"))
			return !isWrapper ? Type.BOOLEAN : Type.WRAPPED_BOOLEAN;
		else if(type.equals("byte"))
			return !isWrapper ? Type.BYTE : Type.WRAPPED_BYTE;
		else if(type.equals("short"))
			return !isWrapper ? Type.SHORT : Type.WRAPPED_SHORT;
		else if(type.equals("int"))
			return !isWrapper ? Type.INT : Type.WRAPPED_INT;
		else if(type.equals("long"))
			return !isWrapper ? Type.LONG : Type.WRAPPED_LONG;
		else if(type.equals("float"))
			return !isWrapper ? Type.FLOAT : Type.WRAPPED_FLOAT;
		else if(type.equals("double"))
			return !isWrapper ? Type.DOUBLE : Type.WRAPPED_DOUBLE;
		else if(type.equals("string"))
			return Type.STRING;
		
		return Type.NULL;
	}

}