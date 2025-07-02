package jredfox.mce;

import java.io.File;

import org.ralleytn.simple.json.JSONArray;
import org.ralleytn.simple.json.JSONObject;

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
	private static byte[] arr_byte = 		{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
	private static short[] arr_short = 		{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
	private static int[] arr_int = 			{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
	private static long[] arr_long = 		{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
	private static float[] arr_float = 		{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
	private static double[] arr_double = 	{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
	private static String[] arr_str = 		{"Entity Id", "Index 1", "Index 2", "Index 3", "Index 4", "Index 5", "Index 6", "Index 7", "Index 8", "Index 9"};
	private static boolean[] arr_tst = 	{false, false, true, false, false, false};
	
	//Start static array wrappers
	private static Boolean[] o_arr_bool = 	{false, false, false, false, false, false};
	private static Boolean[] o_arr_tst = 	{false, false, true, false, false, false};
	private static Byte[] o_arr_byte = 		{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};
	private static Short[] o_arr_short = 	{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};
	private static Integer[] o_arr_int = 	{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};
	private static Long[] o_arr_long = 		{0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L};
	private static Float[] o_arr_float = 	{0F, 1F, 2F, 3F, 4F, 5F, 6F, 7F, 8F, 9F, 10F, 11F, 12F, 13F, 14F, 15F, 16F, 17F, 18F, 19F, 20F};
	private static Double[] o_arr_double = 	{0D, 1D, 2D, 3D, 4D, 5D, 6D, 7D, 8D, 9D, 10D, 11D, 12D, 13D, 14D, 15D, 16D, 17D, 18D, 19D, 20D};
	
	static
	{
		
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
		
		System.out.print("[");
		for(double v : o_arr_double)
			System.out.print(v + ",");
		System.out.println("]");
		
//		System.out.print("[");
//		for(int big : arr_biggums)
//			System.out.print(big + ",");
//		System.out.println("]");
	}

	public static void load() {
		method_st(null);
	}
	
//	public static int[] arr_biggums = new int[Short.MAX_VALUE + 2];
	
	public static void main(String[] args)
	{
//		int size = Short.MAX_VALUE + 1;
//		for(int i=0;i<=size;i++)
//			System.out.print(i + ",");
//		System.out.println();
//		System.out.println((byte)128);
	}

}
