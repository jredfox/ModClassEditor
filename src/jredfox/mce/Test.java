package jredfox.mce;

import org.ralleytn.simple.json.JSONObject;
import org.ralleytn.simple.json.JSONParseException;

public class Test {
	
	private static boolean id_bool = false;
	private static byte id_b = (byte) 202;
	private static short id_s = 201;
	private static int id = 200;
	private static long id_l = 203;
	private static float id_f = 1.0F;
	private static double id_d = 2.0D;
	private static String id_str = "Entity Id";
	
	//Start Wrappers
	private static Boolean wrapped_id_bool = false;
	private static Byte wrapped_id_b = (byte) 125;
	private static Short wrapped_id_s = 126;
	private static Integer wrapped_id = 127;
	private static Long wrapped_id_l = 128L;
	private static Float wrapped_id_f = 2.0F;
	private static Double wrapped_id_d = 3.0D;
	
	//Start static array primatives
	private static boolean[] arr_bool = 	{false, false, false, false, false, false};
	private static byte[] arr_btye = 		{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
	private static short[] arr_short = 		{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
	private static int[] arr_int = 			{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
	private static long[] arr_long = 		{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
	private static float[] arr_float = 		{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
	private static double[] arr_double = 	{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
	private static String[] arr_str = 		{"Entity Id"};
	
	static
	{
		arr_bool[0] = true;
	}
	
//	private int o_id = new Integer(200);
//	private short o_id_s = 201;
//	private byte o_id_b = (byte) 202;
//	private long o_id_l = 203;
//	private String o_id_str = "Entity Id";
	
	private static void method_st(Object obj)
	{
		System.out.println("id:" +     id);
		System.out.println("id_s:" +   id_s);
		System.out.println("id_b:" +   id_b);
		System.out.println("id_l:" +   id_l);
		System.out.println("id_str:" + id_str);
	}

	public static void load() {
		method_st(null);
	}
	
	public static void main(String[] args)
	{
		
	}

}
