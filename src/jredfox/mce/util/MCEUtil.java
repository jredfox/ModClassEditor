package jredfox.mce.util;

public class MCEUtil {
	
	/**
	 * Parse a char Safely
	 */
	public static char parseChar(String value) 
	{
		if(value == null) return 0;
		
		try
		{
			return (char) Long.parseLong(value, 10);
		}
		catch(NumberFormatException num)
		{
			return value.isEmpty() ? ((char) 0) : value.charAt(0);
		}
	}
	
	/**
	 * parses a boolean allowing for 0 to be false and 1 to be true
	 * if string is null or empty it's returning false which is equal to 0 which is also what null converted to a primative is
	 */
	public static boolean parseBoolean(String s)
	{
		if(s == null)
			return false;
        int l = s.length();
        
        //if string is empty return false
        if(l == 0)
        	return false;
        
        //start left side trim if required
        int i = 0;
        while ((i < l) && (s.charAt(i) <= ' ')) {
            i++;
        }
        
        //if string is empty after trim return false
        if(i == l)
        	return false;
        
        char c = s.charAt(i);
        return c == 't' || c == 'T' || c == '1';
	}
	
	/**
	 * Parse a Byte Safely
	 */
	public static byte parseByte(String value) 
	{
		if(value == null) return 0;
		
		try
		{
			return (byte) Long.parseLong(value.trim(), 10);
		}
		catch(NumberFormatException e)
		{
			return 0;
		}
	}
	
	/**
	 * Parse a Short Safely
	 */
	public static short parseShort(String value) 
	{
		if(value == null) return 0;
		
		try
		{
			return (short) Long.parseLong(value.trim(), 10);
		}
		catch(NumberFormatException e)
		{
			return 0;
		}
	}
	
	/**
	 * Parse a Int Safely
	 */
	public static int parseInt(String value) 
	{
		if(value == null) return 0;
		
		try
		{
			return (int) Long.parseLong(value.trim(), 10);
		}
		catch(NumberFormatException e)
		{
			return 0;
		}
	}
	
	/**
	 * Parse a Int Safely
	 */
	public static long parseLong(String value) 
	{
		if(value == null) return 0;
		
		try
		{
			return Long.parseLong(value.trim(), 10);
		}
		catch(NumberFormatException e)
		{
			return 0;
		}
	}
	
	/**
	 * Parse a Int Safely
	 */
	public static float parseFloat(String value) 
	{
		if(value == null) return 0;
		
		try
		{
			return Float.parseFloat(value.trim());
		}
		catch(NumberFormatException e)
		{
			return 0;
		}
	}
	
	public static double parseDouble(String value) 
	{
		if(value == null) return 0;
		
		try
		{
			return Double.parseDouble(value.trim());
		}
		catch(NumberFormatException e)
		{
			return 0;
		}
	}
	
	public static String parseString(String value)
	{
		if(value == null)
			return "";
		
		value = value.trim();
		if(!value.isEmpty() && value.charAt(0) == '"')
		{
			int len = value.length();
			if (len == 1)
				return "";
			
			int end = len - 1;
			if (value.charAt(end) != '"')
				end++;
			return value.substring(1, end);
		}
		
		return value;
	}
	
	public static String[] splitFirst(String s, char delim)
	{
		int index = s.indexOf(delim);
		if(index == 0)
			index = s.indexOf(delim, 1);
		return index == -1 ? new String[]{s, ""} : new String[] {s.substring(0, index), s.substring(index + 1)};
	}

}
