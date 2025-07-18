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
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.ralleytn.simple.json.JSONArray;
import org.ralleytn.simple.json.JSONObject;

import jredfox.mce.util.JSONUtils;
import jredfox.mce.util.MCECoreUtils;

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
	
	/**
	 * Prevent Memory leak delete the entire JSONObject holding everything
	 */
	public void gc()
	{
		this.root = null;
		this.enabled = false;
		this.file_gen = null;
	}
	
	public void gen(String actualName, ClassNode classNode)
	{
		if(!this.enabled) 
			return;
		
		synchronized (this)
		{
			JSONArray fields = new JSONArray();
			root.put(actualName, fields);
			
			MethodNode mcgen = getMCEGen(actualName.replace('.', '/'), classNode, "<clinit>");
			AbstractInsnNode spot = MCECoreUtils.getLastReturn(mcgen);
			for (int i = 0; i <= 1; i++) 
			{
				for (FieldNode fn : classNode.fields) 
				{
					boolean isStatic = (fn.access & Opcodes.ACC_STATIC) != 0;
					if (i == 0 ? (!isStatic) : isStatic)
						continue;
					JSONObject jfield = new JSONObject();
					String type = MCECoreUtils.getType(fn.desc);
					jfield.put("name", fn.name);
					jfield.put("desc", fn.desc);
					jfield.put("static", isStatic);
					jfield.put("type", type);
					fields.add(jfield);
	
					//We need to generate the values here for non static arrays
					if (isStatic && !MCECoreUtils.UNSUPPORTED.equals(type) && !fn.desc.startsWith("[")) 
					{
						InsnList li = new InsnList();
						String clname = actualName.replace('.', '/');
						li.add(new LdcInsnNode(actualName));
						li.add(new LdcInsnNode(fn.name));
						li.add(new FieldInsnNode(Opcodes.GETSTATIC, clname, fn.name, fn.desc));
						li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jredfox/mce/MCEGen", "capValue", "(Ljava/lang/String;Ljava/lang/String;" + fn.desc + ")V"));
						MCECoreUtils.addLabelNode(li);
						mcgen.instructions.insertBefore(spot, li);
					}
				}
			}
			
			//Save Changes in Mod's <clinit> after all fields have their values generated
			InsnList sl = new InsnList();
			sl.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jredfox/mce/MCEGen", "saveChanges", "()V"));
			MCECoreUtils.addLabelNode(sl);
			mcgen.instructions.insertBefore(spot, sl);
			
			//Save Progres
			this.save();
		}
	}
	
	private static final String base = "mce_gen_";
	public MethodNode getMCEGen(String actualName, ClassNode c, String mname) 
	{
		int index = 0;
		while(MCECoreUtils.getMethodNode(c, (base + index), "()V") != null)
			index++;
		String genName = base + index;
		MethodNode m = new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, genName, "()V", null, null);
		InsnList l = new InsnList();
		LabelNode l0 = new LabelNode();
		l.add(l0);
		l.add(new LineNumberNode(0, l0));
		l.add(new InsnNode(Opcodes.RETURN));
		l.add(new LabelNode());
		m.instructions = l;
		m.visitMaxs(0, 0);
		
		//Add the method to the class
		c.methods.add(m);
		
		//Hook Method to Invoke MCEGen
		MethodNode clinit = MCECoreUtils.getMethodNode(c, mname, "()V");
		InsnList list = new InsnList();
		list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, actualName, genName, "()V"));
		clinit.instructions.insertBefore(MCECoreUtils.getLastReturn(clinit), list);
		
		return m;
	}

	public void save()
	{
		JSONUtils.save(this.root, this.file_gen);
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
	
	public static void capValue(String className, String fieldName, char v) {
		Transformer.gen.capValue0(className, fieldName, String.valueOf(v));
	}
	
	public static void capValue(String className, String fieldName, Character v) {
		Transformer.gen.capValue0(className, fieldName, String.valueOf(v));
	}
	
	public static void gc0()
	{
		Transformer.gen.gc();
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
				int line = 0;
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
				l.add(new LineNumberNode(line++, l0));
				l.add(new VarInsnNode(Opcodes.ALOAD, 0));
				l.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V"));
				LabelNode l1 = new LabelNode();
				l.add(l1);
				l.add(new LineNumberNode(line++, l1));
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
				LabelNode l1_ = new LabelNode();
				initList.add(l1_);
				initList.add(new LineNumberNode(line++, l1_));
				initList.add(new InsnNode(Opcodes.RETURN));
				LabelNode l2_ = new LabelNode();
				initList.add(l2_);
				init.instructions = initList;
				init.visitMaxs(0, 0);
				
				//Generate the methods calls dynamically
				for(Map.Entry pair : this.root.entrySet())
				{
					if(!(pair.getValue() instanceof JSONArray))
						continue;
					String clname = (String) pair.getKey();
					JSONArray arr = (JSONArray) pair.getValue();
					if(arr.isEmpty())
						continue;
					
					MethodNode dynamic = getMCEGen(cn, classNode, init.name);
					AbstractInsnNode genSpot = MCECoreUtils.getLastReturn(dynamic);
					for(Object ov : arr)
					{
						if(!(ov instanceof JSONObject))
							continue;
						JSONObject jval = (JSONObject) ov;
						
						//Since we only support static value in gen if this tag exists we don't have to check everything again only this tag
						if(!jval.containsKey("value"))
							continue;
						String fieldName = jval.getString("name");
						String desc = jval.getString("desc");
						
						InsnList li = new InsnList();
						li.add(new LdcInsnNode(clname));
						li.add(new LdcInsnNode(fieldName));
						li.add(new FieldInsnNode(Opcodes.GETSTATIC, clname.replace('.', '/'), fieldName, desc));
						li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jredfox/mce/MCEGen", "capValue", "(Ljava/lang/String;Ljava/lang/String;" + desc + ")V"));
						LabelNode label_gen = new LabelNode();
						li.add(label_gen);
						li.add(new LineNumberNode(line++, label_gen));
						dynamic.instructions.insertBefore(genSpot, li);
					}
				}
				
				InsnList slist = new InsnList();
				slist.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jredfox/mce/MCEGen", "saveChanges", "()V"));
				LabelNode L0Save = new LabelNode();
				slist.add(L0Save);
				slist.add(new LineNumberNode(line++, L0Save));
				//Call GC to prevent memory leak
				if(cn.equals("jredfox/mce/MCEGenInitPost"))
				{
					slist.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jredfox/mce/MCEGen", "gc0", "()V"));
					LabelNode L_0 = new LabelNode();
					slist.add(L_0);
					slist.add(new LineNumberNode(line++, L_0));
				}
				init.instructions.insertBefore(MCECoreUtils.getLastReturn(init), slist);
				
				return MCECoreUtils.toByteArray(MCECoreUtils.getClassWriter(classNode, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES), actualName, null);
			}
			catch(Throwable t)
			{
				t.printStackTrace();
			}
			return null;
		}
	}

}
