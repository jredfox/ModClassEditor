package org.ralleytn.simple.json.internal;

public class NumUtil {
	
	/**
	 * cast without loosing data and have a random negative number
	 */
	public static int castInt(long l)
	{
		if(l > Integer.MAX_VALUE)
			return Integer.MAX_VALUE;
		if(l < Integer.MIN_VALUE)
			return Integer.MIN_VALUE;
		return (int)l;
	}
	public static short castShort(long l)
	{
		if(l > Short.MAX_VALUE)
		{
			return Short.MAX_VALUE;
		}
		else if(l < Short.MIN_VALUE)
		{
			return Short.MIN_VALUE;
		}
		return (short)l;
	}
	public static byte castByte(long l)
	{
		if(l > Byte.MAX_VALUE)
			return Byte.MAX_VALUE;
		if(l < Byte.MIN_VALUE)
			return Byte.MIN_VALUE;
		return (byte)l;
	}
	
	public static short castShort(int i)
	{
		if(i > Short.MAX_VALUE)
		{
			return Short.MAX_VALUE;
		}
		else if(i < Short.MIN_VALUE)
		{
			return Short.MIN_VALUE;
		}
		return (short)i;
	}
	public static byte castByte(int i)
	{
		if(i > Byte.MAX_VALUE)
			return Byte.MAX_VALUE;
		if(i < Byte.MIN_VALUE)
			return Byte.MIN_VALUE;
		return (byte)i;
	}
	public static byte castByte(short s)
	{
		if(s > Byte.MAX_VALUE)
			return Byte.MAX_VALUE;
		if(s < Byte.MIN_VALUE)
			return Byte.MIN_VALUE;
		return (byte)s;
	}
	public static byte castByte(float f) 
	{
		long l = convertToLong(f);
		return castByte(l);
	}
	public static byte castByte(double d) 
	{
		long l = convertToLong(d);
		return castByte(l);
	}
	
	public static short castShort(float f) 
	{
		long l = convertToLong(f);
		return castShort(l);
	}
	public static short castShort(double d) 
	{
		long l = convertToLong(d);
		return castShort(l);
	}
	public static int castInt(float f) 
	{
		long l = convertToLong(f);
		return castInt(l);
	}
	public static int castInt(double d) 
	{
		long l = convertToLong(d);
		return castInt(l);
	}
	public static long castLong(float f)
	{
		return convertToLong(f);
	}
	public static long castLong(double d)
	{
		return convertToLong(d);
	}
	public static float castFloat(double d)
	{
		if(d > Float.MAX_VALUE)
			return Float.MAX_VALUE;
		else if (d < Float.MIN_VALUE)
			return Float.MIN_VALUE;
		return (float)d;
	}
	
	/**
	 * doesn't work every time as java algorithms truncate to 0 sometimes when negative only????
	 */
	public static long convertToLong(double d)
	{
		if(d > Long.MAX_VALUE)
			return Long.MAX_VALUE;
		if(d < Long.MIN_VALUE)
			return Long.MIN_VALUE;
		return Math.round(d);
	}
	/**
	 * doesn't work every time as java algorithms truncate to 0 sometimes when negative only????
	 */
	public static long convertToLong(float f)
	{
		if(f > Long.MAX_VALUE)
			return Long.MAX_VALUE;
		if(f < Long.MIN_VALUE)
			return Long.MIN_VALUE;
		return (long)f;
	}
	
	public static int getInt(Number obj)
	{
		obj = getIntNum(obj);
		if(obj instanceof Long)
			return castInt((Long)obj);
		return obj.intValue();
	}
	public static short getShort(Number obj)
	{
		obj = getIntNum(obj);
		if(obj instanceof Long)
			return castShort((Long)obj);
		else if(obj instanceof Integer)
			return castShort((Integer)obj);
		return obj.shortValue();
	}
	public static byte getByte(Number obj)
	{
		obj = getIntNum(obj);
		if(obj instanceof Long)
			return castByte((Long)obj);
		else if(obj instanceof Integer)
			return castByte((Integer)obj);
		else if(obj instanceof Short)
			return castByte((Short)obj);
		return obj.byteValue();
	}
	
	public static Long getLong(Number obj){
		obj = getIntNum(obj);
		return obj.longValue();
	}
	public static double getDouble(Number obj){
		return obj.doubleValue();
	}
	public static float getFloat(Number obj){
		return obj.floatValue();
	}
	/**
	 * if double/float convert to integer of long else do nothing
	 */
	public static Number getIntNum(Number obj) {
		if(obj instanceof Double)
		{
			obj = new Long(convertToLong((Double)obj));
		}
		else if(obj instanceof Float)
		{
			obj = new Long(convertToLong((Float)obj));
		}
		return obj;
	}

}
