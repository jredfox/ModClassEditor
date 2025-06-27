package jredfox.mce;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.ralleytn.simple.json.JSONArray;
import org.ralleytn.simple.json.JSONObject;

import cpw.mods.fml.relauncher.IClassTransformer;

public class Transformer implements IClassTransformer {
	
	public boolean init;
	public boolean recomputeFrames;
	public boolean generateFieldNames;
	public static MCEGen gen;
	public Map<String, String> arr = new ConcurrentHashMap();
	
	public Transformer()
	{
		this.init();
	}

	@Override
	public byte[] transform(String name, String actualName, byte[] clazz) 
	{
		if(this.generateFieldNames && actualName.startsWith("jredfox.mce.MCEGenInit"))
		{
			try
			{
				String cn = actualName.replace('.', '/');
				ClassNode classNode = new ClassNode();
				//Define Required Class Fields
				classNode.version = Opcodes.V1_6;
				classNode.access = Opcodes.ACC_PUBLIC;
				classNode.name = cn;
				classNode.superName = "java/lang/Object";
				
				//Define Required Default Constructor
				MethodNode ctr = new MethodNode(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
				classNode.methods.add(ctr);
				InsnList l = new InsnList();
				LabelNode l0 = new LabelNode();
				l.add(l0);
				l.add(new VarInsnNode(Opcodes.ALOAD, 0));
				l.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V"));
				LabelNode l1 = new LabelNode();
				l.add(l1);
				l.add(new InsnNode(Opcodes.RETURN));
				LabelNode l2 = new LabelNode();
				l.add(l2);
				ctr.instructions = l;
				ctr.localVariables.add(new LocalVariableNode("this", "L" + cn + ";", null, l0, l2, 0));
				ctr.visitMaxs(1, 1);
				
				//TODO: create the dynamic Gen Method
				
				return CoreUtils.toByteArray(CoreUtils.getClassWriter(classNode, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES), actualName);
			}
			catch(Throwable t)
			{
				t.printStackTrace();
			}
		}
		return (clazz != null && this.arr.containsKey(actualName)) ? configureModClass(actualName, clazz) : clazz;
	}

	public byte[] configureModClass(String actualName, byte[] clazz)
	{
		try
		{
			ClassNode classNode = CoreUtils.getClassNode(clazz);
			CoreUtils.pubMinusFinal(classNode, true);
			int flags = this.recomputeFrames ? (ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES) : ClassWriter.COMPUTE_MAXS;
			gen.gen(actualName, classNode);
			return CoreUtils.toByteArray(CoreUtils.getClassWriter(classNode, flags), actualName);
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
		gen = new MCEGen("config/ModClassEditor/GeneratedFieldNames.json", this.generateFieldNames);
		gen.init();
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
			this.arr.put((String) s, "");
		
		//Load the ModClassEditor Into Objects
		for(String c : this.arr.keySet())
			MCEObj.register(c, json);
	}

}
