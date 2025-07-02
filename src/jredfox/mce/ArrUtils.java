package jredfox.mce;

public class ArrUtils {
	
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
	
	/**
	 * Insert an array into another one replacing it's indexesd
	 */
	public static void insert(Boolean[] arr, Boolean[] val, int index)
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
	public static void insert(Byte[] arr, Byte[] val, int index)
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
	public static void insert(Short[] arr, Short[] val, int index)
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
	public static void insert(Integer[] arr, Integer[] val, int index)
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
	public static void insert(Long[] arr, Long[] val, int index)
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
	public static void insert(Float[] arr, Float[] val, int index)
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
	public static void insert(Double[] arr, Double[] val, int index)
	{
		if(arr.length == 0)
			return;
		if(index == -1)
			index = arr.length - 1;
		
		int valIndex = 0;
	    for (int i = index; i < arr.length && valIndex < val.length; i++) 
	        arr[i] = val[valIndex++];
	}
	
	public static boolean[] newArr_Z(String[] values)
	{
		return (boolean[]) newArr(values, DataType.BOOLEAN);
	}
	
	public static byte[] newArr_B(String[] values)
	{
		return (byte[]) newArr(values, DataType.BYTE);
	}
	
	public static short[] newArr_S(String[] values)
	{
		return (short[]) newArr(values, DataType.SHORT);
	}
	
	public static int[] newArr_I(String[] values)
	{
		return (int[]) newArr(values, DataType.INT);
	}
	
	public static long[] newArr_J(String[] values)
	{
		return (long[]) newArr(values, DataType.LONG);
	}
	
	public static float[] newArr_F(String[] values)
	{
		return (float[]) newArr(values, DataType.FLOAT);
	}
	
	public static double[] newArr_D(String[] values)
	{
		return (double[]) newArr(values, DataType.DOUBLE);
	}
	
	/**
	 * Dynamic new array creation assuming you have the string values
	 */
	public static Object newArr(String[] values, DataType type)
	{
		int size = values.length;
		
		switch(type)
		{
			case BOOLEAN:
			{
				boolean[] arr = new boolean[size];
				for(int i=0;i<size;i++)
					arr[i] = Boolean.parseBoolean(values[i]);
				return arr;
			}
			case BYTE:
			{
				byte[] arr = new byte[size];
				for(int i=0;i<size;i++)
					arr[i] = MCEObj.parseByte(values[i]);
				return arr;
			}
			case SHORT:
			{
				short[] arr = new short[size];
				for(int i=0;i<size;i++)
					arr[i] = MCEObj.parseShort(values[i]);
				return arr;
			}
			case INT:
			{
				int[] arr = new int[size];
				for(int i=0;i<size;i++)
					arr[i] = MCEObj.parseInt(values[i]);
				return arr;
			}
			case LONG:
			{
				long[] arr = new long[size];
				for(int i=0;i<size;i++)
					arr[i] = MCEObj.parseLong(values[i]);
				return arr;
			}
			case FLOAT:
			{
				float[] arr = new float[size];
				for(int i=0;i<size;i++)
					arr[i] = MCEObj.parseFloat(values[i]);
				return arr;
			}
			case DOUBLE:
			{
				double[] arr = new double[size];
				for(int i=0;i<size;i++)
					arr[i] = MCEObj.parseDouble(values[i]);
				return arr;
			}
			case STRING:
			{
				String[] arr = new String[size];
				for(int i=0;i<size;i++)
					arr[i] = values[i];
				return arr;
			}
			case WRAPPED_BOOLEAN:
			{
				Boolean[] arr = new Boolean[size];
				for(int i=0;i<size;i++)
					arr[i] = Boolean.parseBoolean(values[i]);
				return arr;
			}
			case WRAPPED_BYTE:
			{
				Byte[] arr = new Byte[size];
				for(int i=0;i<size;i++)
					arr[i] = MCEObj.parseByte(values[i]);
				return arr;
			}
			case WRAPPED_SHORT:
			{
				Short[] arr = new Short[size];
				for(int i=0;i<size;i++)
					arr[i] = MCEObj.parseShort(values[i]);
				return arr;
			}
			case WRAPPED_INT:
			{
				Integer[] arr = new Integer[size];
				for(int i=0;i<size;i++)
					arr[i] = MCEObj.parseInt(values[i]);
				return arr;
			}
			case WRAPPED_LONG:
			{
				Long[] arr = new Long[size];
				for(int i=0;i<size;i++)
					arr[i] = MCEObj.parseLong(values[i]);
				return arr;
			}
			case WRAPPED_FLOAT:
			{
				Float[] arr = new Float[size];
				for(int i=0;i<size;i++)
					arr[i] = MCEObj.parseFloat(values[i]);
				return arr;
			}
			case WRAPPED_DOUBLE:
			{
				Double[] arr = new Double[size];
				for(int i=0;i<size;i++)
					arr[i] = MCEObj.parseDouble(values[i]);
				return arr;
			}
			
			default:
				break;
		}
		return null;
	}

}