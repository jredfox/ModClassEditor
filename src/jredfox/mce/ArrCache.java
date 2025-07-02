package jredfox.mce;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Used for large arrays whose size is > 10 to avoid the bytecode instruction limit
 */
public class ArrCache {
	
	/**
	 * Map of ID --> Static Array which Examples: boolean[] Boolean[] int[] Integer[] etc.... 
	 * There are no checks on security for performance reasons but please don't add anything that's not an array
	 */
	public static Map<Integer, Object> cache = new ConcurrentHashMap<Integer, Object>();
	private static final AtomicInteger id = new AtomicInteger();
	
	public static int register(Object val) 
	{
		int newId = id.getAndIncrement();
		cache.put(newId, val);
		return newId;
	}
	
	/**
	 * @return Static Array given an ID
	 */
	public static Object get(int id)
	{
		Object o = cache.get(id);
		cache.remove(id);
		return o;
	}

}
