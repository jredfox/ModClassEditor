package jredfox.mce;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.ralleytn.simple.json.JSONArray;
import org.ralleytn.simple.json.JSONObject;

import cpw.mods.fml.relauncher.IClassTransformer;

public class Transformer implements IClassTransformer {
	
	public boolean init;
	public boolean recomputeFrames;
	public boolean generateFieldNames;
	public Map<String, Object> arr = new ConcurrentHashMap();
	
	public Transformer()
	{
		this.init();
	}

	@Override
	public byte[] transform(String name, String actualName, byte[] clazz) 
	{
		if(clazz == null)
			return null;
		
		return this.arr.containsKey(actualName) ? configureModClass(actualName, clazz) : clazz;
	}

	public byte[] configureModClass(String actualName, byte[] clazz)
	{
		try
		{
			ClassNode classNode = CoreUtils.getClassNode(clazz);
			CoreUtils.pubMinusFinal(classNode, true);
			int flags = this.recomputeFrames ? (ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES) : ClassWriter.COMPUTE_MAXS;
			
			if(this.generateFieldNames)
			{
				File file_gen = new File(System.getProperty("user.dir"), "config/ModClassEditor/GeneratedFieldNames.json");
				if(!file_gen.exists())
					JSONUtils.save(new JSONObject(), file_gen);
				JSONObject groot = JSONUtils.getJson(file_gen);
				JSONArray fields = new JSONArray();
				groot.put(actualName, fields);
				
				for(int i=0;i<=1;i++)
				{
					for(FieldNode fn : classNode.fields)
					{
						boolean isStatic = (fn.access & Opcodes.ACC_STATIC) != 0;
						if(i == 0 ? (!isStatic) : isStatic)
							continue;
						JSONObject jfield = new JSONObject();
						String type = this.getType(fn.desc);
						jfield.put("name", fn.name);
						jfield.put("desc", fn.desc);
						jfield.put("static", isStatic);
						jfield.put("type", type);
						fields.add(jfield);
					}
				}
				
				JSONUtils.save(groot, file_gen);
			}
			
			return CoreUtils.toByteArray(CoreUtils.getClassWriter(classNode, flags), actualName);
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
		return clazz;
	}

	private static final String UNSUPPORTED = "Unsupported";
	public String getType(String desc) {
		return desc.equals("B") ? "byte"
				: desc.equals("S") ? "short"
						: desc.equals("I") ? "int"
								: desc.equals("J") ? "long"
										: desc.equals("Ljava/lang/String;") ? "string"
												: desc.equals("Z") ? "boolean" 
												: desc.equals("F") ? "float"
												: desc.equals("D") ? "double"
														: UNSUPPORTED;
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
		
		//Initialize the config for the first time
		if(!cfg.exists())
		{
			JSONObject json = new JSONObject();
			json.put("Recompute Frames", true);
			json.put("Generate Field Names", false);
			JSONUtils.save(json, cfg);
		}
		
		JSONObject ojson = JSONUtils.getJson(cfg);
		this.recomputeFrames = ojson.getBoolean("Recompute Frames");
		this.generateFieldNames = ojson.getBoolean("Generate Field Names");
		System.out.println("Recompute frames:" + this.recomputeFrames + ", GenFieldNames:" + this.generateFieldNames);
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
			this.arr.put((String) s, null);
		
		//Load the ModClassEditor Into Objects
		for(String c : this.arr.keySet())
			MCEObj.register(c, json);
	}

}
