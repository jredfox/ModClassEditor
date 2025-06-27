package jredfox.mce;

import java.io.File;
import java.util.Map;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.ralleytn.simple.json.JSONArray;
import org.ralleytn.simple.json.JSONObject;

public class MCEGen {
	
	public File file_gen;
	public JSONObject root;
	public boolean enabled;
	public String stage = "";
	
	public MCEGen(String path, boolean enabled)
	{
		this.file_gen = new File(System.getProperty("user.dir"), path);
		this.enabled = enabled;
	}
	
	public void init()
	{
		if(!this.enabled) 
			return;
		
		this.root = new JSONObject();
	}
	
	public void gen(String actualName, ClassNode classNode)
	{
		if(!this.enabled) 
			return;
		
		synchronized (this)
		{
			MethodNode clinit = CoreUtils.getMethodNode(classNode, "<clinit>", "()V");
			JSONArray fields = new JSONArray();
			root.put(actualName, fields);
			
			//Save Changes in Mod's <clinit> after all fields have their values generated
			AbstractInsnNode spot = CoreUtils.getLastReturn(clinit);
			clinit.instructions.insertBefore(spot, new MethodInsnNode(Opcodes.INVOKESTATIC, "jredfox/mce/MCEGen", "saveChanges", "()V"));
	
			for (int i = 0; i <= 1; i++) 
			{
				for (FieldNode fn : classNode.fields) 
				{
					boolean isStatic = (fn.access & Opcodes.ACC_STATIC) != 0;
					if (i == 0 ? (!isStatic) : isStatic)
						continue;
					JSONObject jfield = new JSONObject();
					String type = this.getType(fn.desc);
					jfield.put("name", fn.name);
					jfield.put("desc", fn.desc);
					jfield.put("static", isStatic);
					jfield.put("type", type);
					fields.add(jfield);
	
					//We need to generate the values here for non static arrays
					if (isStatic && !UNSUPPORTED.equals(type) && !fn.desc.startsWith("[")) 
					{
						InsnList li = new InsnList();
						String clname = actualName.replace('.', '/');
						li.add(new LdcInsnNode(actualName));
						li.add(new LdcInsnNode(fn.name));
						li.add(new FieldInsnNode(Opcodes.GETSTATIC, clname, fn.name, fn.desc));
						li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jredfox/mce/MCEGen", "capValue", "(Ljava/lang/String;Ljava/lang/String;" + fn.desc + ")V"));
						clinit.instructions.insertBefore(spot, li);
					}
				}
			}
			
			//Save Progres
			this.save();
		}
	}
	
	public void save()
	{
		JSONUtils.save(this.root, this.file_gen);
	}

	static final String UNSUPPORTED = "Unsupported";

	public String getType(String desc) {
		return desc.equals("B") ? "byte"
				: desc.equals("S") ? "short"
						: desc.equals("I") ? "int"
								: desc.equals("J") ? "long"
										: desc.equals("Ljava/lang/String;") ? "string"
												: desc.equals("Z") ? "boolean"
														: desc.equals("F") ? "float"
																: desc.equals("D") ? "double"
																		: desc.equals("Ljava/lang/Boolean;") ? "Boolean"
																: desc.equals("Ljava/lang/Byte;") ? "Byte"
															: desc.equals("Ljava/lang/Short;") ? "Short"
														: desc.equals("Ljava/lang/Integer;") ? "Integer"
												: desc.equals("Ljava/lang/Long;") ? "Long"
										: desc.equals("Ljava/lang/Float;") ? "Float"
								: desc.equals("Ljava/lang/Double;") ? "Double"
						: UNSUPPORTED;
	}
	
	public static void saveChanges() {
		Transformer.gen.save();
	}
	
	public static void capValue(String className, String fieldName, boolean v) {
		Transformer.gen.capValue0(className, fieldName, v);
	}
	
	public static void capValue(String className, String fieldName, byte v) {
		Transformer.gen.capValue0(className, fieldName, v);
	}
	
	public static void capValue(String className, String fieldName, short v) {
		Transformer.gen.capValue0(className, fieldName, v);
	}
	
	public static void capValue(String className, String fieldName, int v) {
		Transformer.gen.capValue0(className, fieldName, v);
	}
	
	public static void capValue(String className, String fieldName, long v) {
		Transformer.gen.capValue0(className, fieldName, v);
	}
	
	public static void capValue(String className, String fieldName, float v) {
		Transformer.gen.capValue0(className, fieldName, v);
	}
	
	public static void capValue(String className, String fieldName, double v) {
		Transformer.gen.capValue0(className, fieldName, v);
	}
	
	public static void capValue(String className, String fieldName, String v) {
		Transformer.gen.capValue0(className, fieldName, v);
	}
	
	public static void capValue(String className, String fieldName, Boolean v) {
		Transformer.gen.capValue0(className, fieldName, v);
	}
	
	public static void capValue(String className, String fieldName, Byte v) {
		Transformer.gen.capValue0(className, fieldName, v);
	}
	
	public static void capValue(String className, String fieldName, Short v) {
		Transformer.gen.capValue0(className, fieldName, v);
	}
	
	public static void capValue(String className, String fieldName, Integer v) {
		Transformer.gen.capValue0(className, fieldName, v);
	}
	
	public static void capValue(String className, String fieldName, Long v) {
		Transformer.gen.capValue0(className, fieldName, v);
	}
	
	public static void capValue(String className, String fieldName, Float v) {
		Transformer.gen.capValue0(className, fieldName, v);
	}
	
	public static void capValue(String className, String fieldName, Double v) {
		Transformer.gen.capValue0(className, fieldName, v);
	}

	private void capValue0(String className, String fieldName, Object v)
	{
		synchronized (this)
		{
			JSONArray arr = this.root.getJSONArray(className);
			
			//Handle Error
			if(arr == null)
			{
				System.err.println("ClassName not Generated Yet! " + className);
				arr = new JSONArray();
				this.root.put(className, arr);
			}
			
			//replace the field's value with the updated one
			for(Object o : arr)
			{
				JSONObject j = (JSONObject) o;
				if(j.getString("name").equals(fieldName))
				{
					j.put(this.stage.isEmpty() ? "value" : ("value_" + this.stage), v);
					break;
				}
			}
		}
	}

	public byte[] hookGenInit(String actualName, byte[] org) 
	{
		if(!this.enabled || org == null)
			return org;
		
		synchronized (this)
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
				
				//Generate init method to load the values into MCEGen
				MethodNode init = new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "init", "()V", null, null);
				classNode.methods.add(init);
				InsnList initList = new InsnList();
				LabelNode l0_ = new LabelNode();
				initList.add(l0_);
				initList.insert(l0_, new MethodInsnNode(Opcodes.INVOKESTATIC, "jredfox/mce/MCEGen", "saveChanges", "()V"));
				LabelNode l1_ = new LabelNode();
				initList.add(l1_);
				initList.add(new InsnNode(Opcodes.RETURN));
				LabelNode l2_ = new LabelNode();
				initList.add(l2_);
				init.instructions = initList;
				init.localVariables.add(new LocalVariableNode("this", "L" + cn + ";", null, l0_, l2_, 0));
				init.visitMaxs(1, 1);
				
				//Generate the methods calls dynamically
				for(Map.Entry pair : this.root.entrySet())
				{
					if(!(pair.getValue() instanceof JSONArray))
						continue;
					JSONArray arr = (JSONArray) pair.getValue();
					for(Object ov : arr)
					{
						if(!(ov instanceof JSONObject))
							continue;
						JSONObject jval = (JSONObject) ov;
						
						//Since we only support static value in gen if this tag exists we don't have to check everything again only this tag
						if(!jval.containsKey("value"))
							continue;
						boolean isStatic = jval.getBoolean("static");
						String orgClName = (String) pair.getKey();
						String clname = orgClName.toString().replace('.', '/');
						String fieldName = jval.getString("name");
						String desc = jval.getString("desc");
						String type = jval.getString("type");
						
						InsnList li = new InsnList();
						li.add(new LdcInsnNode(orgClName));
						li.add(new LdcInsnNode(fieldName));
						li.add(new FieldInsnNode(Opcodes.GETSTATIC, clname, fieldName, desc));
						li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jredfox/mce/MCEGen", "capValue", "(Ljava/lang/String;Ljava/lang/String;" + desc + ")V"));
						init.instructions.insert(l0_, li);
					}
				}
				
				return CoreUtils.toByteArray(CoreUtils.getClassWriter(classNode, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES), actualName);
			}
			catch(Throwable t)
			{
				t.printStackTrace();
			}
			return null;
		}
	}

}
