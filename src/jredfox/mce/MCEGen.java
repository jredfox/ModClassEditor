package jredfox.mce;

import java.io.File;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.ralleytn.simple.json.JSONArray;
import org.ralleytn.simple.json.JSONObject;

public class MCEGen {
	
	public File file_gen;
	public JSONObject root;
	public boolean enabled;
	
	public MCEGen(String path, boolean enabled)
	{
		this.file_gen = new File(System.getProperty("user.dir"), path);
		this.enabled = enabled;
	}
	
	public void init()
	{
		if(!this.enabled) 
			return;
		
		if (!file_gen.exists())
			JSONUtils.save(new JSONObject(), file_gen);
		root = JSONUtils.getJson(file_gen);
		if(root == null)
		{
			root = new JSONObject();
			JSONUtils.save(root, file_gen);
		}
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
			AbstractInsnNode spot = CoreUtils.getLastLabelNode(clinit, false);
			clinit.instructions.insert(spot, new MethodInsnNode(Opcodes.INVOKESTATIC, "jredfox/mce/MCEGen", "saveChanges", "()V"));
	
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
						clinit.instructions.insert(spot, li);
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
					j.put("value", v);
					break;
				}
			}
		}
	}

}
