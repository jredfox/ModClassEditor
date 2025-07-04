package jredfox.mce;

public class ArrUtils {
	
	public static void set(char[] arr, int index, char val)
	{
		if(index == -1)
			index = arr.length - 1;
		arr[index] = val;
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
	
	public static void set(Character[] arr, int index, char val)
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
	
	public static void fill(char[] arr, char val, int start, int end, int increment) 
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
	
	public static void fill(Character[] arr, char val, int start, int end, int increment) 
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
	public static void insert(char[] arr, char[] val, int index)
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
	public static void insert(Character[] arr, char[] val, int index)
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
	public static void insert(Character[] arr, Character[] val, int index)
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
				{
					String s = values[i];
					if(s != null)
						arr[i] = Boolean.parseBoolean(s);
				}
				return arr;
			}
			case BYTE:
			{
				byte[] arr = new byte[size];
				for(int i=0;i<size;i++)
				{
					String s = values[i];
					if(s != null)
						arr[i] = MCEObj.parseByte(s);
				}
				return arr;
			}
			case SHORT:
			{
				short[] arr = new short[size];
				for(int i=0;i<size;i++)
				{
					String s = values[i];
					if(s != null)
						arr[i] = MCEObj.parseShort(s);
				}
				return arr;
			}
			case INT:
			{
				int[] arr = new int[size];
				for(int i=0;i<size;i++)
				{
					String s = values[i];
					if(s != null)
						arr[i] = MCEObj.parseInt(s);
				}
				return arr;
			}
			case LONG:
			{
				long[] arr = new long[size];
				for(int i=0;i<size;i++)
				{
					String s = values[i];
					if(s != null)
						arr[i] = MCEObj.parseLong(s);
				}
				return arr;
			}
			case FLOAT:
			{
				float[] arr = new float[size];
				for(int i=0;i<size;i++)
				{
					String s = values[i];
					if(s != null)
						arr[i] = MCEObj.parseFloat(s);
				}
				return arr;
			}
			case DOUBLE:
			{
				double[] arr = new double[size];
				for(int i=0;i<size;i++)
				{
					String s = values[i];
					if(s != null)
						arr[i] = MCEObj.parseDouble(s);
				}
				return arr;
			}
			case STRING:
			{
				String[] arr = new String[size];
				for(int i=0;i<size;i++)
					arr[i] = values[i];//it's ok for string to not skip null values as it doesn't parse anything
				return arr;
			}
			case CHAR:
			{
				char[] arr = new char[size];
				for(int i=0;i<size;i++)
				{
					String s = values[i];
					if(s != null)
						arr[i] = MCEObj.parseChar(s);
				}
				return arr;
			}
			case WRAPPED_BOOLEAN:
			{
				Boolean[] arr = new Boolean[size];
				for(int i=0;i<size;i++)
				{
					String s = values[i];
					if(s != null)
						arr[i] = Boolean.parseBoolean(s);
				}
				return arr;
			}
			case WRAPPED_BYTE:
			{
				Byte[] arr = new Byte[size];
				for(int i=0;i<size;i++)
				{
					String s = values[i];
					if(s != null)
						arr[i] = MCEObj.parseByte(s);
				}
				return arr;
			}
			case WRAPPED_SHORT:
			{
				Short[] arr = new Short[size];
				for(int i=0;i<size;i++)
				{
					String s = values[i];
					if(s != null)
						arr[i] = MCEObj.parseShort(s);
				}
				return arr;
			}
			case WRAPPED_INT:
			{
				Integer[] arr = new Integer[size];
				for(int i=0;i<size;i++)
				{
					String s = values[i];
					if(s != null)
						arr[i] = MCEObj.parseInt(s);
				}
				return arr;
			}
			case WRAPPED_LONG:
			{
				Long[] arr = new Long[size];
				for(int i=0;i<size;i++)
				{
					String s = values[i];
					if(s != null)
						arr[i] = MCEObj.parseLong(s);
				}
				return arr;
			}
			case WRAPPED_FLOAT:
			{
				Float[] arr = new Float[size];
				for(int i=0;i<size;i++)
				{
					String s = values[i];
					if(s != null)
						arr[i] = MCEObj.parseFloat(s);
				}
				return arr;
			}
			case WRAPPED_DOUBLE:
			{
				Double[] arr = new Double[size];
				for(int i=0;i<size;i++)
				{
					String s = values[i];
					if(s != null)
						arr[i] = MCEObj.parseDouble(s);
				}
				return arr;
			}
			case WRAPPED_CHAR:
			{
				Character[] arr = new Character[size];
				for(int i=0;i<size;i++)
				{
					String s = values[i];
					if(s != null)
						arr[i] = MCEObj.parseChar(s);
				}
				return arr;
			}
			
			default:
				break;
		}
		return null;
	}

	public static void print(Object o)
	{
		System.out.print("[");
		if(o instanceof char[])
		{
			char[] arr = (char[]) o;
			for(int i=0;i<arr.length;i++)
			{
				String c = (i + 1) < arr.length ? "," : "";
				System.out.print(arr[i] + c);
			}
		}
		if(o instanceof boolean[])
		{
			boolean[] arr = (boolean[]) o;
			for(int i=0;i<arr.length;i++)
			{
				String c = (i + 1) < arr.length ? "," : "";
				System.out.print(arr[i] + c);
			}
		}
		else if(o instanceof byte[])
		{
			byte[] arr = (byte[]) o;
			for(int i=0;i<arr.length;i++)
			{
				String c = (i + 1) < arr.length ? "," : "";
				System.out.print(arr[i] + c);
			}
		}
		else if(o instanceof short[])
		{
			short[] arr = (short[]) o;
			for(int i=0;i<arr.length;i++)
			{
				String c = (i + 1) < arr.length ? "," : "";
				System.out.print(arr[i] + c);
			}
		}
		else if(o instanceof int[])
		{
			int[] arr = (int[]) o;
			for(int i=0;i<arr.length;i++)
			{
				String c = (i + 1) < arr.length ? "," : "";
				System.out.print(arr[i] + c);
			}
		}
		else if(o instanceof long[])
		{
			long[] arr = (long[]) o;
			for(int i=0;i<arr.length;i++)
			{
				String c = (i + 1) < arr.length ? "," : "";
				System.out.print(arr[i] + c);
			}
		}
		else if(o instanceof float[])
		{
			float[] arr = (float[]) o;
			for(int i=0;i<arr.length;i++)
			{
				String c = (i + 1) < arr.length ? "," : "";
				System.out.print(arr[i] + c);
			}
		}
		else if(o instanceof double[])
		{
			double[] arr = (double[]) o;
			for(int i=0;i<arr.length;i++)
			{
				String c = (i + 1) < arr.length ? "," : "";
				System.out.print(arr[i] + c);
			}
		}
		else if(o instanceof String[])
		{
			String[] arr = (String[]) o;
			for(int i=0;i<arr.length;i++)
			{
				String c = (i + 1) < arr.length ? "," : "";
				System.out.print(arr[i] + c);
			}
		}
		else if(o instanceof Character[])
		{
			Character[] arr = (Character[]) o;
			for(int i=0;i<arr.length;i++)
			{
				String c = (i + 1) < arr.length ? "," : "";
				System.out.print(arr[i] + c);
			}
		}
		else if(o instanceof Boolean[])
		{
			Boolean[] arr = (Boolean[]) o;
			for(int i=0;i<arr.length;i++)
			{
				String c = (i + 1) < arr.length ? "," : "";
				System.out.print(arr[i] + c);
			}
		}
		else if(o instanceof Byte[])
		{
			Byte[] arr = (Byte[]) o;
			for(int i=0;i<arr.length;i++)
			{
				String c = (i + 1) < arr.length ? "," : "";
				System.out.print(arr[i] + c);
			}
		}
		else if(o instanceof Short[])
		{
			Short[] arr = (Short[]) o;
			for(int i=0;i<arr.length;i++)
			{
				String c = (i + 1) < arr.length ? "," : "";
				System.out.print(arr[i] + c);
			}
		}
		else if(o instanceof Integer[])
		{
			Integer[] arr = (Integer[]) o;
			for(int i=0;i<arr.length;i++)
			{
				String c = (i + 1) < arr.length ? "," : "";
				System.out.print(arr[i] + c);
			}
		}
		else if(o instanceof Long[])
		{
			Long[] arr = (Long[]) o;
			for(int i=0;i<arr.length;i++)
			{
				String c = (i + 1) < arr.length ? "," : "";
				System.out.print(arr[i] + c);
			}
		}
		else if(o instanceof Float[])
		{
			Float[] arr = (Float[]) o;
			for(int i=0;i<arr.length;i++)
			{
				String c = (i + 1) < arr.length ? "," : "";
				System.out.print(arr[i] + c);
			}
		}
		else if(o instanceof Double[])
		{
			Double[] arr = (Double[]) o;
			for(int i=0;i<arr.length;i++)
			{
				String c = (i + 1) < arr.length ? "," : "";
				System.out.print(arr[i] + c);
			}
		}
		System.out.println("]");
	}

}