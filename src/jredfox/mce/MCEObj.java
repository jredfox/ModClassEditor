package jredfox.mce;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.ralleytn.simple.json.JSONArray;
import org.ralleytn.simple.json.JSONObject;

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
//		this.frs.clear();
//		this.params.clear();
		
		JSONArray arr = json.getJSONArray("Fields");
		for(Object o : arr)
		{
			if(!(o instanceof JSONObject))
				continue;//Why are there comments in here
			
			JSONObject f = (JSONObject) o;
			this.fields.add(new MCEField(f));
		}
	}

	public static class MCEField
	{
		/**
		 * The name of the Field
		 */
		public String name;
		/**
		 * the value to replace
		 */
		public String value;
		/**
		 * the type [string, boolean, byte, short, int, long, float, double, Boolean, Byte, Short, Integer, Long, Float, Double]
		 */
		public String type;
		/**
		 * The method to inject into
		 */
		public String method;
		/**
		 * The method desc to inject into
		 */
		public String desc;
		/**
		 * the injection point
		 */
		public String inject;
		
		public MCEField()
		{
			
		}
		
		public MCEField(JSONObject json)
		{
			this(json.getString("name"), json.getAsString("value"), json.getString("type"), json.getString("method"), json.getString("desc"), json.getString("inject"));
		}
		
		public MCEField(String name, String value, String type, String method, String desc, String inject)
		{
			this.name = name;
			this.value = value;
			this.type = this.safeString(type);
			this.method = this.safeString(method, "<clinit>");
			this.desc = this.safeString(desc);
			this.inject = this.safeString(inject, "after");
		}
		
		private String safeString(String s)
		{
			return this.safeString(s, "");
		}
		
		private String safeString(String s, String def)
		{
			return (s == null || s.isEmpty()) ? def : s;
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
		
		//Configure Fields
		for(MCEField f : mce.fields)
		{
			String type = f.type;
			FieldNode fn = CoreUtils.getFieldNode(f.name, classNode);
			
			//Sanity Checks
			if(fn == null)
			{
				System.err.println("Field not Found:" + f.name + " in: " + mce.className);
				continue;
			}
			else if((fn.access & Opcodes.ACC_STATIC) == 0)
			{
				System.err.println("Object Fields are not Supported for Editing yet as it's more complex and Per Object to Edit:" + f.name + " in:" + mce.className);
				continue;
			}
			
			//Populate the Type
			if(type.isEmpty())
				type = getType(fn.desc);
			
			//disallow unsupported field opperations to prevent runtime crashing
			if(type == UNSUPPORTED)
			{
				System.err.println("Unsupported Type for Field:" + f.name + " in:" + mce.className);
				continue;
			}
			
			//Actual code
			MethodNode m = getMethodNode(classNode, f.method, f.desc);
			if(m == null)
			{
				System.err.println("Method Not Found:" + f.method + " desc:\"" + f.desc + "\"");
				continue;
			}
			boolean isWrapper = Character.isUpperCase(type.charAt(0));
			InsnList list = new InsnList();
			if(type.equalsIgnoreCase("boolean"))
			{
				list.add(new InsnNode(Boolean.parseBoolean(f.value) ? Opcodes.ICONST_1 : Opcodes.ICONST_0));
				if(isWrapper)
					list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;"));
				list.add(new FieldInsnNode(Opcodes.PUTSTATIC, mce.classNameASM, f.name, (isWrapper ? "Ljava/lang/Boolean;" : "Z") ));
			}
			else if(type.equalsIgnoreCase("byte"))
			{
				byte b = parseByte(f.value);
				InsnNode insn = getConstantInsn(b);
				if(insn != null)
					list.add(insn);
				else
					list.add(new IntInsnNode(Opcodes.BIPUSH, b));
				
				if(isWrapper)
					list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;"));
				
				list.add(new FieldInsnNode(Opcodes.PUTSTATIC, mce.classNameASM, f.name, (isWrapper ? "Ljava/lang/Byte;" : "B") ));
			}
			else if(type.equals("short"))
			{
				short s = parseShort(f.value);
				InsnNode insn = getConstantInsn(s);
				if(insn != null)
					list.add(insn);
				else if (s >= Byte.MIN_VALUE && s <= Byte.MAX_VALUE)
					list.add(new IntInsnNode(Opcodes.BIPUSH, s));
				else
					list.add(new IntInsnNode(Opcodes.SIPUSH, s));
				list.add(new FieldInsnNode(Opcodes.PUTSTATIC, mce.classNameASM, f.name, "S"));
			}
			else if(type.equals("int"))
			{
				int v = parseInt(f.value);
				InsnNode insn = getConstantInsn(v);
				if(insn != null)
					list.add(insn);
				else if (v >= Byte.MIN_VALUE && v <= Byte.MAX_VALUE)
					list.add(new IntInsnNode(Opcodes.BIPUSH, v));
				else if (v >= Short.MIN_VALUE && v <= Short.MAX_VALUE)
					list.add(new IntInsnNode(Opcodes.SIPUSH, v));
				else
					list.add(new LdcInsnNode(new Integer(v)));
				list.add(new FieldInsnNode(Opcodes.PUTSTATIC, mce.classNameASM, f.name, "I"));
			}
			else if(type.equals("long"))
			{
				long v = parseLong(f.value);
				InsnNode insn = getConstantInsn(v);
				if(insn != null)
					list.add(insn);
				else
					list.add(new LdcInsnNode(new Long(v)));
				list.add(new FieldInsnNode(Opcodes.PUTSTATIC, mce.classNameASM, f.name, "J"));
			}
			else if(type.equals("float"))
			{
				float v = parseFloat(f.value);
				InsnNode insn = getConstantInsn(v);
				if(insn != null)
					list.add(insn);
				else
					list.add(new LdcInsnNode(new Float(v)));
				list.add(new FieldInsnNode(Opcodes.PUTSTATIC, mce.classNameASM, f.name, "F"));
			}
			else if(type.equals("double"))
			{
				double v = Double.parseDouble(f.value);
				InsnNode insn = getConstantInsn(v);
				if(insn != null)
					list.add(insn);
				else
					list.add(new LdcInsnNode(new Double(v)));
				list.add(new FieldInsnNode(Opcodes.PUTSTATIC, mce.classNameASM, f.name, "D"));
			}
			else if(type.equals("string"))
			{
				list.add(new LdcInsnNode(f.value));
				list.add(new FieldInsnNode(Opcodes.PUTSTATIC, mce.classNameASM, f.name, "Ljava/lang/String;"));
			}
			
			//Injection Point
			if(f.inject.equals("after"))
				m.instructions.insertBefore(CoreUtils.getLastReturn(m), list);
			else if(f.inject.equals("before"))
			{
				list.insert(new LabelNode());
				m.instructions.insert(list);
			}
		}
	}

	/**
	 * longs behave completly different in the bytecode compared to boolean, byte, short and int
	 */
	private static InsnNode getConstantInsn(long v) 
	{
		return v == 0 ? new InsnNode(Opcodes.LCONST_0) : (v == 1 ? new InsnNode(Opcodes.LCONST_1) : null);
	}
	
	private static InsnNode getConstantInsn(float v) 
	{
		return v == 0 ? new InsnNode(Opcodes.FCONST_0) : (v == 1 ? new InsnNode(Opcodes.FCONST_1) : (v == 2 ? new InsnNode(Opcodes.FCONST_2) : null) );
	}
	
	private static InsnNode getConstantInsn(double v) 
	{
		return v == 0 ? new InsnNode(Opcodes.DCONST_0) : (v == 1 ? new InsnNode(Opcodes.DCONST_1) : null);
	}

	private static InsnNode getConstantInsn(int b) 
	{
		if (b > -2 && b < 6)
		{
			int opcode = 0;
			switch(b)
			{
				case -1:
					opcode = Opcodes.ICONST_M1;
				break;
				
				case 0:
					opcode = Opcodes.ICONST_0;
				break;
				
				case 1:
					opcode = Opcodes.ICONST_1;
				break;
				
				case 2:
					opcode = Opcodes.ICONST_2;
				break;
				
				case 3:
					opcode = Opcodes.ICONST_3;
				break;
				
				case 4:
					opcode = Opcodes.ICONST_4;
				break;
				
				case 5:
					opcode = Opcodes.ICONST_5;
				break;
			}
			return new InsnNode(opcode);
		}
		
		return null;
	}

	public static MethodNode getMethodNode(ClassNode classNode, String method_name, String method_desc) 
	{
		boolean mt = method_desc.isEmpty() || method_desc.equals("*");
		for (Object method_ : classNode.methods)
		{
			MethodNode method = (MethodNode) method_;
			if (method.name.equals(method_name) && (mt || method.desc.equals(method_desc)) )
			{
				return method;
			}
		}
		return null;
	}
	
	public static final String UNSUPPORTED = "Unsupported";

	public static String getType(String desc) {
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
	
	//Start UTIL METHODS__________________________________________
	//____________________________________________________________
	
	/**
	 * Parse a Byte Safely
	 */
	private static byte parseByte(String value) 
	{
		return (byte) Long.parseLong(value, 10);
	}
	
	/**
	 * Parse a Short Safely
	 */
	private static short parseShort(String value) 
	{
		return (short) Long.parseLong(value, 10);
	}
	
	/**
	 * Parse a Int Safely
	 */
	private static int parseInt(String value) 
	{
		return (int) Long.parseLong(value, 10);
	}
	
	/**
	 * Parse a Int Safely
	 */
	private static long parseLong(String value) 
	{
		return Long.parseLong(value, 10);
	}
	
	/**
	 * Parse a Int Safely
	 */
	private static float parseFloat(String value) 
	{
		return Float.parseFloat(value);
	}
	
	//END UTIL METHODS___________________________________________________
	//___________________________________________________________________

}
