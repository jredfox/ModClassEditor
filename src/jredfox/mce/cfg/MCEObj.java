package jredfox.mce.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.ralleytn.simple.json.JSONArray;
import org.ralleytn.simple.json.JSONObject;

import jredfox.mce.Transformer;
import jredfox.mce.util.MCECoreUtils;

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
	public static void register(String c, JSONObject root, Transformer t) 
	{
		MCEObj.registry.put(c, new MCEObj(c, getCFG(root, c, '.', '/'), t) );
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
	public AtMod atmod;
	public AtNetworkMod atNetworkMod;
	public List<MCEField> fields = new ArrayList();
	protected Transformer transformer;
	
	public MCEObj(String className, Transformer t)
	{
		this.className = className.replace('/', '.');
		this.classNameASM = className.replace('.', '/');
		this.transformer = t;
	}
	
	public MCEObj(String c, JSONObject json, Transformer t)
	{
		this(c, t);
		this.parse(json);
	}
	
	public void parse(JSONObject json)
	{
		if(json == null)
			return;
		
		//clear previous fields
		this.fields.clear();
		
		//Parse the @Mod Configuration Object
		if(json.containsKey("@Mod"))
			this.atmod = new AtMod(json.getJSONObject("@Mod"));
		else if(json.containsKey("modid"))
			this.atmod = new AtMod(json.getString("modid"));
		//Parse the @NetworkMod Configuration Object
		if(json.containsKey("@NetworkMod"))
			this.atNetworkMod = new AtNetworkMod(json.getJSONObject("@NetworkMod"));
		
		Transformer t = this.transformer;//avoid duplicate GETFIELD
		JSONArray arr = json.getJSONArray("Fields");
		if(arr != null)
		{
			for(Object o : arr)
			{
				if(!(o instanceof JSONObject))
					continue;//Why are there comments in here
				
				JSONObject j = (JSONObject) o;
				MCEField f = !j.containsKey("values") ? new MCEField(this, j) : new MCEArrField(this, j);
				this.fields.add(f);
				
				//Auto AT Custom Classes Who are not in ModClasses
				if(f.custom && t != null)
				{
					String key = f.owner.replace('/', '.');
					if(!t.arr.containsKey(key))
						t.at.put(key, "");
				}
			}
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
		this.configureModAnn(cn);
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
			for(int i=cSize - 1; i >= 0; i--)
			{
				MCEField c = cache.get(i);
				if(ds)
					c.genDynamicSetter();
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

	private static final String ATMOD = "Lcpw/mods/fml/common/Mod;";
	private static final String ATNET = "Lcpw/mods/fml/common/network/NetworkMod;";
	private void configureModAnn(ClassNode cn) 
	{
		AtNetworkMod atn = this.atNetworkMod;
		AtMod atmod = this.atmod;
		if(atn == null && atmod == null)
			return;
		
		boolean hasAtn = atn != null;
		boolean hasAtMod = atmod != null;
		boolean foundNet = false;
		List[] arr = {cn.visibleAnnotations, cn.invisibleAnnotations};
		for(List ol : arr)
		{
			if(ol == null || ol.isEmpty())
				continue;
			List<AnnotationNode> l = ol;
			
			for(AnnotationNode ann : l)
			{
				if(hasAtn && ATNET.equals(ann.desc))
				{
					Map<String, Object> vals = MCECoreUtils.getAnnMap(ann);
					if(atn.hasCS)
						vals.put("clientSideRequired", atn.clientSideRequired);
					if(atn.hasSS)
						vals.put("serverSideRequired", atn.serverSideRequired);
					if(atn.hasVB)
						vals.put("versionBounds", atn.versionBounds);
					ann.values = MCECoreUtils.toAnnList(vals);
					foundNet = true;
				}
				else if(hasAtMod && ATMOD.equals(ann.desc))
				{
					Map<String, Object> vals = MCECoreUtils.getAnnMap(ann);
					vals.putAll(atmod.values);
					ann.values = MCECoreUtils.toAnnList(vals);
				}
			}
		}
		
		//Add @NetworkMod if it's not found
		if(hasAtn && !foundNet)
		{
			AnnotationNode atnet = new AnnotationNode("Lcpw/mods/fml/common/network/NetworkMod;");
			atnet.values = new ArrayList(5);
			atnet.values.add("clientSideRequired");
			atnet.values.add(atn.clientSideRequired);
			atnet.values.add("serverSideRequired");
			atnet.values.add(atn.serverSideRequired);
			if(atn.hasVB)
			{
				atnet.values.add("versionBounds");
				atnet.values.add(atn.versionBounds);
			}
			cn.visibleAnnotations.add(atnet);
			
			System.out.println("ANN:" + atn.clientSideRequired + " " + atn.serverSideRequired + " " + atn.versionBounds);
		}
	}

}