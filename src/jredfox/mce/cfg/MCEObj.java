package jredfox.mce.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.ralleytn.simple.json.JSONArray;
import org.ralleytn.simple.json.JSONObject;

import jredfox.mce.Transformer;

/**
 * Allows Classes Fields to be edited as if they were a configuration file
 * @author jredfox
 */
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
	
	/**
	 * Get the MCEObj required for the Transformer
	 */
	public static MCEObj get(String actualName) 
	{
		return registry.get(actualName);
	}
	
	public String className;
	public String classNameASM;
	public List<MCEField> fields = new ArrayList();
//	public List<MCEFindAndReplace> frs = new ArrayList();
//	public List<MCEParam> params = new ArrayList();
	
	public MCEObj(String className)
	{
		this.className = className.replace('/', '.');
		this.classNameASM = className.replace('.', '/');
	}
	
	public MCEObj(String c, JSONObject json)
	{
		this(c);
		this.parse(json);
	}
	
	public void parse(JSONObject json)
	{
		if(json == null)
			return;
		
		//clear previous fields
		this.fields.clear();
		
		JSONArray arr = json.getJSONArray("Fields");
		for(Object o : arr)
		{
			if(!(o instanceof JSONObject))
				continue;//Why are there comments in here
			
			JSONObject f = (JSONObject) o;
			this.fields.add(!f.containsKey("values") ? new MCEField(this, f) : new MCEArrField(this, f));
		}
	}

	public static void configure(String actualName, ClassNode classNode)
	{
		System.out.println("Mod Class Editor:" + actualName);
		MCEObj mce = get(actualName);
		
		//Sanity Check
		if(mce == null) 
		{
			System.err.println("Error Missing " + actualName + " MCEObj from ModClassEditor.JSON this is a BUG!");
			return;
		}
		
		mce.configure(classNode);
	}

	public synchronized void configure(ClassNode cn)
	{
		//Avoid GETFIELD OPCODES
		List<MCEField> cf = this.fields;
		int len = cf.size() - 1;
		
		List<MethodNode> ml = Transformer.ds ? new ArrayList(cn.methods) : cn.methods;
		int size = ml.size();
		int mi = 0;
		
		for(MethodNode m : ml)
		{
			boolean last = (mi++ + 1) == size;
			List<MCEField> cache = new ArrayList(5);
			Map<InsertionPoint, MethodNode> dsc = new HashMap(5);
			for(int i=len; i >= 0; i--)
			{
				MCEField c = cf.get(i);
				if(c.accept(cn, dsc, m))
					cache.add(c);
				else if(last)
					c.clear();
			}
			
			if(Transformer.label)
			{
				for(MCEField c : cache)
				{
					c.apply();
				}
				
				for(MCEField c : cache)
				{
					c.applyLabel();
					if(last)
						c.clear();
				}
			}
			else
			{
				for(MCEField c : cache)
				{
					c.apply();
					if(last)
						c.clear();
				}
			}
		}
	}

}