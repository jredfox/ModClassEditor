package jredfox.mce;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.ralleytn.simple.json.JSONArray;
import org.ralleytn.simple.json.JSONObject;

import cpw.mods.fml.relauncher.IClassTransformer;
import jredfox.mce.cfg.MCEObj;
import jredfox.mce.util.JSONUtils;
import jredfox.mce.util.MCECoreUtils;

public class Transformer implements IClassTransformer {
	
	public boolean init;
	public static boolean recomputeFrames;
	public static boolean generateFieldNames;
	public static boolean label;
	public static boolean gc;
	public static boolean dump;
	public static boolean dumpOrg;
	public static boolean batchLoad;
	public static boolean ds;
	public static MCEGen gen;
	public Map<String, String> arr = new ConcurrentHashMap();
	
	public Transformer()
	{
		this.init();
	}

	@Override
	public byte[] transform(String name, String actualName, byte[] clazz) 
	{
		if(actualName.startsWith("jredfox.mce.MCEGenInit"))
			return this.gen.hookGenInit(actualName, clazz);
		
		return (clazz != null && this.arr.containsKey(actualName)) ? configureModClass(actualName, clazz) : clazz;
	}

	public byte[] configureModClass(String actualName, byte[] clazz)
	{
		try
		{
			ClassNode classNode = MCECoreUtils.getClassNode(clazz);
			MCECoreUtils.pubMinusFinal(classNode, true);
			int flags = this.recomputeFrames ? (ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES) : ClassWriter.COMPUTE_MAXS;
			gen.gen(actualName, classNode);
			MCEObj.configure(actualName, classNode);
			return MCECoreUtils.toByteArray(MCECoreUtils.getClassWriter(classNode, flags), actualName, clazz);
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
		return clazz;
	}
	
	/**
	 * Initializes the Main Configuration file so we know what we want to edit
	 */
	public void init() 
	{
		this.init_options();
		this.init_mce();
	}

	public void init_options()
	{
		File cfg = new File(System.getProperty("user.dir"), "config/ModClassEditor/options.json");
		JSONObject ojson = JSONUtils.getJson(cfg);
		int size = ojson.size();
		
		this.recomputeFrames = JSONUtils.getorGenBoolean(ojson, "Recompute Frames", true);
		this.generateFieldNames = JSONUtils.getorGenBoolean(ojson, "Generate Field Names", false);
		this.label = JSONUtils.getorGenBoolean(ojson, "Generate Labels", true);
		this.gc = JSONUtils.getorGenBoolean(ojson, "GC MCEField Optimizations", true);
		this.dump = JSONUtils.getorGenBoolean(ojson,"ASM Dump", false) || Boolean.parseBoolean(System.getProperty("asm.dump", "false"));
		this.dumpOrg = JSONUtils.getorGenBoolean(ojson,"ASM Dump Original", false);
		this.batchLoad = JSONUtils.getorGenBoolean(ojson, "Batch Load", true);
		this.ds = JSONUtils.getorGenBoolean(ojson, "Dynamic Setters", true);
				
		//Detect new Config Options
		if(size != ojson.size())
			JSONUtils.save(ojson, cfg);
		
		gen = new MCEGen("config/ModClassEditor/GeneratedFieldNames.json", this.generateFieldNames);
		gen.init();
		System.out.println("Recompute frames:" + this.recomputeFrames + ", GenFieldNames:" + this.generateFieldNames);
		System.out.println("GC:" + this.gc + " Dump:" + this.dump + " DumpOrg:" + this.dumpOrg + " BatchLoad:" + this.batchLoad);
	}
	
	/**
	 * Load the ModClassEditor.json
	 */
	public void init_mce() 
	{
		this.arr.clear();
		File cfg = new File(System.getProperty("user.dir"), "config/ModClassEditor/ModClassEditor.json");
		
		//Create ModClassEditor.json if it does not exist
		if(!cfg.exists())
		{
			JSONObject j = new JSONObject();
			JSONArray a = new JSONArray();
			a.add("com.example.ExampleMod");
			j.put("ModClasses", a);
			JSONUtils.save(j, cfg);
		}
		
		//Load the Classes List
		JSONObject json = JSONUtils.getJson(cfg);
		for(Object s : json.getJSONArray("ModClasses"))
			this.arr.put(((String) s).replace('/', '.'), "");
		
		//Load the ModClassEditor Into Objects
		for(String c : this.arr.keySet())
			MCEObj.register(c, json);
	}

	public static void batchLoad()
	{
		if (!Transformer.batchLoad)
			return;
		System.out.println("MCE Batch Loading....");
		ClassLoader cl = MCECoreUtils.class.getClassLoader();
		for (String c : MCEObj.registry.keySet()) 
		{
			try 
			{
				Class clazz = Class.forName(c, false, cl);
			}
			catch (ClassNotFoundException e) 
			{
				System.err.println("ClassNotFound:" + c);
			} 
			catch (Throwable t) 
			{
				t.printStackTrace();
			}
		}
	}

}
