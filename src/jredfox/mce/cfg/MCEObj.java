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
		MCEObj.registry.put(c, new MCEObj(c, getCFG(root, c, '.', '/')) );
	}
	
	/**
	 * Get the MCEObj required for the Transformer
	 */
	public static MCEObj get(String actualName) 
	{
		return registry.get(actualName);
	}
	
	private static JSONObject getCFG(JSONObject root, String key, char oc, char nc)
	{
		Object o = root.get(key);
		return o != null ? ((JSONObject) o) : ((JSONObject) root.get(key.replace(oc, nc)));
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
		boolean ds = Transformer.ds;
		boolean labels = Transformer.label;
		List<MCEField> cf = this.fields;
		int cfSize = cf.size();
		
		List<MethodNode> ml = ds ? new ArrayList(cn.methods) : cn.methods;
		int size = ml.size();
		int mi = 0;
		
		for(MethodNode m : ml)
		{
			boolean last = (mi++ + 1) == size;
			List<MCEField> cache = new ArrayList(5);
			Map<CachedInsertionPoint, MethodNode> dsc = new HashMap(5);
			for(int i=0; i < cfSize; i++)
			{
				MCEField c = cf.get(i);
				if(c.accept(cn, dsc, m))
					cache.add(c);
				else if(last)
					c.clear();
			}
			//Optimization for when no MCEFields accept the method
			if(cache.isEmpty())
				continue;
			
			int cSize = cache.size();
			for(int i=0; i < cSize; i++)
			{
				MCEField c = cache.get(i);
				if(c.cip.opp == Opperation.BEFORE)
				{
					if(ds)
						c.genDynamicSetter();
					else
						c.apply();
				}
			}
			
			for(int i=cSize - 1; i >= 0; i--)
			{
				MCEField c = cache.get(i);
				if(ds)
				{
					if(c.ocip == null && c.cip.opp.isAfter())
						c.genDynamicSetter();
					c.apply();
				}
				else if(c.cip.opp.isAfter())
					c.apply();
			}
			
			if(labels || last)
			{
				for(int i=0; i < cSize; i++)
				{
					MCEField c = cache.get(i);
					if(labels)
						c.applyLabel();
					if(last)
						c.clear();
				}
			}
		}
	}

}