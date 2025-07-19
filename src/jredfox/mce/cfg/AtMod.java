package jredfox.mce.cfg;

import java.util.HashMap;
import java.util.Map;

import org.ralleytn.simple.json.JSONObject;

import jredfox.mce.util.MCEUtil;

public class AtMod {
	
	public Map<String, Object> values = new HashMap();
	
	public AtMod()
	{
		
	}
	
	public AtMod(String modid)
	{
		this.put("modid", modid);
	}
	
	public AtMod(JSONObject j)
	{
		for(Map.Entry<Object, Object> e : j.entrySet())
		{
			Object v = e.getValue();
			this.put((String)e.getKey(), (v == null ? "" : v.toString()) );
		}
	}
	
	public void put(String key, String v)
	{
		if(key.equals("useMetadata"))
		{
			this.values.put(key, MCEUtil.parseBoolean(v));
		}
		else
			this.values.put(key, v);
	}

	public void apply(Map<String, Object> vals)
	{
		vals.putAll(this.values);
	}

}
