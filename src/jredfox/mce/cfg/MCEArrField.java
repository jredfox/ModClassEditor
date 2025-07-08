package jredfox.mce.cfg;

import java.util.List;

import org.ralleytn.simple.json.JSONObject;

import jredfox.mce.util.MCEUtil;

public class MCEArrField extends MCEField
{
	/**
	 * The list of values going to be applied to the static array
	 */
	public String[] values;
	/**
	 * whether or not the values has a null value used to support wrapper objects and strings
	 */
	public boolean hasNULL;
	/**
	 * used for static arrays as the start index where -1 represents the last index no matter how large or small
	 */
	public int index_start;
	/**
	 * used for static arrays as the end index where -1 repsresents the last index
	 */
	public int index_end;
	/**
	 * used for static arrays as it increments the value by this number each time
	 */
	public int increment;
	/**
	 * when true allows the array to grow WARNING: this creates a new memory location and possible code breaking changes
	 */
	public boolean grow;
	/**
	 * when true inserts values into the index without replacement and grows the array WARNING: this creates a new memory location and possible code breaking changes
	 */
	public boolean append;
	/**
	 * when true replaces the entire array with your values WARNING: this creates a new memory location and possible code breaking changes
	 */
	public boolean replace;
	
	public MCEArrField(){}
	
	public MCEArrField(JSONObject json)
	{
		this(json.getString("name"), json.getJSONArray("values"), json.getString("type"), json.getString("method"), json.getString("desc"), new InsertionPoint(json), json.getAsString("index"), json.getAsString("increment"));
	}
		
	public MCEArrField(String name, List values, String type, String method, String desc, InsertionPoint inject, String index, String increment)
	{
		super(name, null, type, method, desc, inject);
			
		//process values into the String[] array
		if(values != null && !values.isEmpty())
		{
			this.values = new String[values.size()];
			for(int i=0;i<values.size();i++)
			{
				Object o = values.get(i);
				if(o != null)
					this.values[i] = String.valueOf(o);
				else
					this.hasNULL = true;
			}
		}
		else
			this.values = new String[] {""};
		
		//process index
		String[] arr = MCEUtil.splitFirst(MCEUtil.safeString(index, "0").replace("start", "0"), '-');
		String str_start = arr[0];
		String str_end = arr[1];
		this.index_start = str_start.equals("end") ? -1 : MCEUtil.parseInt(str_start);
		this.index_end = str_end.isEmpty() ? this.index_start : (str_end.equals("end") ? -1 : MCEUtil.parseInt(str_end));
			
			//process increment
		this.increment = MCEUtil.parseInt(MCEUtil.safeString(increment, "0"));
	}
		
	@Override
	public void gc() 
	{
		this.values = null;
	}
}