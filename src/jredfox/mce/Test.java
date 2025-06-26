package jredfox.mce;

import org.ralleytn.simple.json.JSONFormatter;
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
	
	static
	{
//		MCEGen.capValue("", "", id_bool);
//		id_b = (byte) 100;
//		id_s = 100;
//		id = 100;
//		id_l = 100;
//		id_f = 100F;
//		id_d = 100D;
//		String id_str = "A String";
//		MCEGen.capValue("", "", "");
//		MCEGen.capValue("", "", "");
//		MCEGen.saveChanges();
	}
	
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
