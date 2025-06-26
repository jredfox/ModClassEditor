package jredfox.mce;

import org.ralleytn.simple.json.JSONFormatter;
import org.ralleytn.simple.json.JSONObject;

public class Test {
	
	private static int id = 200;
	private static short id_s = 201;
	private static byte id_b = (byte) 202;
	private static long id_l = 203;
	private static String id_str = "Entity Id";
	
	private static boolean id_bool = false;
	private static float id_f = 1.0F;
	private static double id_d = 2.0D;
	
	private int o_id = 200;
	private short o_id_s = 201;
	private byte o_id_b = (byte) 202;
	private long o_id_l = 203;
	private String o_id_str = "Entity Id";
	
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
