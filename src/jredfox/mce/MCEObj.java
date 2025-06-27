package jredfox.mce;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ralleytn.simple.json.JSONArray;
import org.ralleytn.simple.json.JSONObject;

public class MCEObj {
	
	public static final Map<String, MCEObj> registry = new HashMap();
	
	/**
	 * Register an MCEObj
	 * @param class name
	 * @param JSONObject root
	 */
	public static void register(String c, JSONObject root) 
	{
		MCEObj.registry.put(c, new MCEObj(c, root.getJSONObject(c)));
	}
	
	public String className;
	public List<MCEField> fields = new ArrayList();
//	public List<MCEFindAndReplace> frs = new ArrayList();
//	public List<MCEParam> params = new ArrayList();
	
	public MCEObj(String className)
	{
		this.className = className;
	}
	
	public MCEObj(String c, JSONObject json)
	{
		this.className = c;
		this.parse(json);
	}
	
	public void parse(JSONObject json)
	{
		if(json == null)
			return;
		
		//clear previous fields
		this.fields.clear();
//		this.frs.clear();
//		this.params.clear();
		
		JSONArray arr = json.getJSONArray("Fields");
		for(Object o : arr)
		{
			if(!(o instanceof JSONObject))
				continue;//Why are there comments in here
			
			JSONObject f = (JSONObject) o;
			this.fields.add(new MCEField(f));
		}
	}

	public static class MCEField
	{
		/**
		 * The name of the Field
		 */
		public String name;
		/**
		 * the value to replace
		 */
		public String value;
		/**
		 * the type [string, boolean, byte, short, int, long, float, double, Boolean, Byte, Short, Integer, Long, Float, Double]
		 */
		public String type;
		/**
		 * The method to inject into
		 */
		public String method;
		/**
		 * The method desc to inject into
		 */
		public String desc;
		/**
		 * the injection point
		 */
		public String inject;
		
		public MCEField()
		{
			
		}
		
		public MCEField(JSONObject json)
		{
			this(json.getString("name"), json.getString("value"), json.getString("type"), json.getString("method"), json.getString("desc"), json.getString("inject"));
		}
		
		public MCEField(String name, String value, String type, String method, String desc, String inject)
		{
			this.name = name;
			this.value = value;
			this.type = this.safeString(type);
			this.method = this.safeString(method, "<clinit>");
			this.desc = this.safeString(desc);
			this.inject = this.safeString(inject, "after");
		}
		
		private String safeString(String s)
		{
			return this.safeString(s, "");
		}
		
		private String safeString(String s, String def)
		{
			return s != null ? s : def;
		}
	}

}
