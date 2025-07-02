package jredfox.mce;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
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
			this.fields.add(!f.containsKey("values") ? new MCEField(f) : new MCEArrField(f));
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
		
		protected String safeString(String s)
		{
			return this.safeString(s, "");
		}
		
		protected String safeString(String s, String def)
		{
			return (s == null || s.isEmpty()) ? def : s;
		}

		public void gc() {}
	}
	
	public static class MCEArrField extends MCEField
	{
		/**
		 * The list of values going to be applied to the static array
		 */
		public String[] values;
		/**
		 * used for static arrays as the start index where -1 represents the last index no matter how large or small
		 */
		public int index_start;
		/**
		 * used for static arrays as the end index where -1 repsresents the last index
		 */
		public int index_end;
		/**
		 * used for static arrays as it increments the value by this number each time
		 */
		public int increment;
		/**
		 * when true allows the array to grow WARNING: this creates a new memory location and possible code breaking changes
		 */
		public boolean grow;
		/**
		 * when true inserts values into the index without replacement and grows the array WARNING: this creates a new memory location and possible code breaking changes
		 */
		public boolean append;
		/**
		 * when true replaces the entire array with your values WARNING: this creates a new memory location and possible code breaking changes
		 */
		public boolean replace;
		
		public MCEArrField(){}
		
		public MCEArrField(JSONObject json)
		{
			this(json.getString("name"), json.getJSONArray("values"), json.getString("type"), json.getString("method"), json.getString("desc"), json.getString("inject"), json.getAsString("index"), json.getAsString("increment"));
		}
		
		public MCEArrField(String name, List values, String type, String method, String desc, String inject, String index, String increment)
		{
			super(name, null, type, method, desc, inject);
			
			//process values into the String[] array
			if(values != null && !values.isEmpty())
			{
				this.values = new String[values.size()];
				for(int i=0;i<values.size();i++)
					this.values[i] = String.valueOf(values.get(i));
			}
			else
				this.values = new String[] {""};
			
			//process index
			String[] arr = splitFirst(this.safeString(index, "0"), '-');
			String str_start = arr[0];
			String str_end = arr[1];
			this.index_start = str_start.equals("end") ? -1 : Integer.parseInt(str_start);
			this.index_end = str_end.isEmpty() ? this.index_start : (str_end.equals("end") ? -1 : Integer.parseInt(str_end));
			
			//process increment
			this.increment = parseInt(this.safeString(increment, "0"));
		}
		
		@Override
		public void gc() 
		{
			this.values = null;
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
			String str_type = f.type;
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
			if(str_type.isEmpty())
				str_type = getType(fn.desc);
			
			//Convert the type into a useable thing
			boolean  isArr = str_type.startsWith("[");
			DataType type = DataType.getType(isArr ? str_type.replace("[", "") : str_type);
			
			//disallow unsupported field opperations to prevent runtime crashing
			if(type == DataType.NULL)
			{
				System.err.println("Unsupported Type for Field:" + f.name + " in:" + mce.className);
				continue;
			}
			
			//Actual code
			List<MethodNode> methodList = getMethodNodes(classNode, f.method, f.desc);
			if(methodList.isEmpty())
			{
				System.err.println("Method Not Found:" + f.method + " desc:\"" + f.desc + "\"");
				continue;
			}
			
			for(MethodNode m : methodList)
			{
				InsnList list = new InsnList();
				if(!isArr)
				{
					list.add(getNumInsn(f.value, type));
					if(type.isWrapper)
						list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, type.clazz, "valueOf", type.descValueOf));
					list.add(new FieldInsnNode(Opcodes.PUTSTATIC, mce.classNameASM, f.name, type.desc));
				}
				//static array support
				else
				{
					MCEArrField farr = (MCEArrField) f;
					DataType arr_type = type;
					list.add(new FieldInsnNode(Opcodes.GETSTATIC, mce.classNameASM, f.name, fn.desc));//arr_short
					if(farr.values.length < 2)
					{
						if(farr.index_start != farr.index_end)
						{
							//ArrUtils#fill(arr, v, index_start, index_end, increment); or ArrUtils#fill(arr, v, index_start, index_end);
							list.add(getNumInsn(farr.values[0], arr_type));//value
							list.add(getIntInsn(farr.index_start));//index_start
							list.add(getIntInsn(farr.index_end));//index_end
							if(arr_type.hasIncrement)
								list.add(getIntInsn(farr.increment));//inecrement
							list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jredfox/mce/ArrUtils", "fill", arr_type.descFill));
						}
						else
						{
							//arr_short[index_start] = v;
							//or ArrUtils#set(arr, index, v);
							list.add(getIntInsn(farr.index_start));//set the index
							list.add(getNumInsn(farr.values[0], arr_type));//set the value
							if(farr.index_start > -1)
							{
								//convert the primitive datatype into it's object form before using AASTORE
								if(arr_type.isWrapper)
									list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, arr_type.clazz, "valueOf", arr_type.descValueOf));
								list.add(new InsnNode(arr_type.arrayStore));//stores the value
							}
							else
								list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jredfox/mce/ArrUtils", "set", arr_type.descSet));
						}
					}
					else
					{
						//ArrUtils#insert(arr, new arr[]{this.values}, farr.index_start);
						genStaticArraySafe(list, farr.values, arr_type, false);
						list.add(getIntInsn(farr.index_start));
						list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jredfox/mce/ArrUtils", "insert", arr_type.descInsert));
					}
				}
				
				//Injection Point
				if(f.inject.equals("after"))
				{
					addLabelNode(list);
					m.instructions.insert(CoreUtils.getLastReturn(m).getPrevious(), list);
				}
				else if(f.inject.equals("before"))
				{
					insertLabelNode(list);
					m.instructions.insert(list);
				}
			}
			
			//prevents memory leaks of arrays and allows the GC to clean it later
			f.gc();
		}
	}

	/**
	 * Generates a Static Array Safely and if biggere then size of 10 will generate it now and cache it to prevent exceeding the bytecode limit
	 */
	public static void genStaticArraySafe(InsnList list, String[] values, DataType type, boolean wrappers)
	{
		if(values.length < 11)
		{
			genStaticArray(list, values, type, wrappers);
			return;
		}
		if(!wrappers)
			type = DataType.getPrimitive(type);
		int id = ArrCache.register(ArrUtils.newArr(values, type));
		list.add(getIntInsn(id));
		list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jredfox/mce/ArrCache", "get", "(I)Ljava/lang/Object;"));
		list.add(new TypeInsnNode(Opcodes.CHECKCAST, "[" + type.desc));
	}

	/**
	 * Generates a static array initialized with specified values
	 * @param list the list to generate the bytecode into list#add will be called so keep that in mind no injections points will be present
	 * @param values a string list of values representing any data type and will get parsed based on the type param
	 * @param type defines what data type of static array to create
	 * @WARNING: DO NOT USE past 10 indexes as Java's max bytecode limit per method is 65535 bytes. This method works but is depreciated please use the ArrUtils#gen instead
	 */
	public static void genStaticArray(InsnList list, String[] values, DataType type, boolean wrappers) 
	{
		if(!wrappers)
			type = DataType.getPrimitive(type);
		
		//array size
		list.add(getIntInsn(values.length));
		
		//array type
		if(!type.isObject)
			list.add(new IntInsnNode(Opcodes.NEWARRAY, type.newArrayType));
		else
			list.add(new TypeInsnNode(Opcodes.ANEWARRAY, type.clazz));
		
		//initialize NON-ZERO VALUES
		boolean isWrapper = type.isWrapper;
		for(int index=0; index < values.length; index++)
		{
			String str_v = values[index];
			AbstractInsnNode valInsn = null;
			switch(type)
			{
				case WRAPPED_BOOLEAN:
				case BOOLEAN:
				{
					boolean v = Boolean.parseBoolean(str_v);
					if(!v && !isWrapper)
						continue;
					valInsn = getBoolInsn(v);
				}
				break;
				case WRAPPED_BYTE:
				case BYTE:
				{
					byte v = parseByte(str_v);
					if(v == 0 && !isWrapper)
						continue;
					valInsn = getIntInsn(v);
				}
				break;
				case WRAPPED_SHORT:
				case SHORT:
				{
					short v = parseShort(str_v);
					if(v == 0 && !isWrapper)
						continue;
					valInsn = getIntInsn(v);
				}
				break;
				case WRAPPED_INT:
				case INT:
				{
					int v = parseInt(str_v);
					if(v == 0 && !isWrapper)
						continue;
					valInsn = getIntInsn(v);
				}
				break;
				case WRAPPED_LONG:
				case LONG:
				{
					long v = parseLong(str_v);
					if(v == 0 && !isWrapper)
						continue;
					valInsn = getLongInsn(v);
				}
				break;
				case WRAPPED_FLOAT:
				case FLOAT:
				{
					float v = parseFloat(str_v);
					if(v == 0 && !isWrapper)
						continue;
					valInsn = getFloatInsn(v);
				}
				break;
				case WRAPPED_DOUBLE:
				case DOUBLE:
				{
					double v = parseDouble(str_v);
					if(v == 0 && !isWrapper)
						continue;
					valInsn = getDoubleInsn(v);
				}
				break;
				case STRING:
					valInsn = new LdcInsnNode(str_v);
				break;
				
				default:
					break;
			}
			
			list.add(new InsnNode(Opcodes.DUP));
			list.add(getIntInsn(index));//indexes are always integer and follow the same rules as any boolean - int values on pushing
			list.add(valInsn);//changes based on the data type
			if(isWrapper)
				list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, type.clazz, "valueOf", type.descValueOf));//converts the datatype into it's Object form
			list.add(new InsnNode(type.arrayStore));
		}
	}
	
	public static AbstractInsnNode getNumInsn(String str_v, DataType type)
	{
		switch(type)
		{
			case WRAPPED_BOOLEAN:
			case BOOLEAN:
				return getBoolInsn(Boolean.parseBoolean(str_v));
			case WRAPPED_BYTE:
			case BYTE:
				return getIntInsn(parseByte(str_v));
			case WRAPPED_SHORT:
			case SHORT:
				return getIntInsn(parseShort(str_v));
			case WRAPPED_INT:
			case INT:
				return getIntInsn(parseInt(str_v));
			case WRAPPED_LONG:
			case LONG:
				return getLongInsn(parseLong(str_v));
			case WRAPPED_FLOAT:
			case FLOAT:
				return getFloatInsn(parseFloat(str_v));
			case WRAPPED_DOUBLE:
			case DOUBLE:
				return getDoubleInsn(parseDouble(str_v));
			case STRING:
				return new LdcInsnNode(str_v);
			default:
				break;
		}
		return null;
	}

	public static InsnNode getBoolInsn(boolean v) 
	{
		return new InsnNode(v ? Opcodes.ICONST_1 : Opcodes.ICONST_0);
	}

	public static AbstractInsnNode getIntInsn(int v) 
	{
		InsnNode insn = getConstantInsn(v);
		if(insn != null)
			return insn;
		else if (v >= Byte.MIN_VALUE && v <= Byte.MAX_VALUE)
			return new IntInsnNode(Opcodes.BIPUSH, v);
		else if (v >= Short.MIN_VALUE && v <= Short.MAX_VALUE)
			return new IntInsnNode(Opcodes.SIPUSH, v);
		return new LdcInsnNode(new Integer(v));
	}
	
	public static AbstractInsnNode getLongInsn(long v) 
	{
		AbstractInsnNode cst = getConstantInsn(v);
		return cst == null ? new LdcInsnNode(new Long(v)) : cst;
	}
	
	public static AbstractInsnNode getFloatInsn(float v)
	{
		AbstractInsnNode cst = getConstantInsn(v);
		return cst == null ? new LdcInsnNode(new Float(v)) : cst;
	}
	
	public static AbstractInsnNode getDoubleInsn(double v)
	{
		AbstractInsnNode cst = getConstantInsn(v);
		return cst == null ? new LdcInsnNode(new Double(v)) : cst;
	}

	/**
	 * longs, double and float behave completely different in the bytecode compared to boolean, byte, short and int
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

	public static List<MethodNode> getMethodNodes(ClassNode classNode, String method_name, String method_desc) {
		boolean wc = method_name.contains("*") || method_name.contains("?");
		boolean wcd = method_desc.contains("*") || method_desc.contains("?");
		boolean mt = method_desc.trim().isEmpty();

		List<MethodNode> l = new ArrayList<MethodNode>(wc ? 10 : 3);
		for (Object method_ : classNode.methods) {
			MethodNode method = (MethodNode) method_;

			if ((wc ? WildCardMatcher.match(method.name, method_name, true) : method.name.equals(method_name))
					&& (mt || (wcd ? WildCardMatcher.match(method.desc, method_desc, true)
							: method.desc.equals(method_desc)))) {
				l.add(method);
				if (!wc)
					break;
			}
		}
		return l;
	}
	
	public static final String UNSUPPORTED = "Unsupported";

	public static String getType(String desc) {
		//Static array support
		if(desc.startsWith("[")){
			String type = getType(desc.replace("[", ""));
			return UNSUPPORTED.equals(type) ? UNSUPPORTED : (desc.substring(0, desc.lastIndexOf('[') + 1) + type);
		}
		
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
	
	public static void addLabelNode(InsnList list)
	{
		LabelNode l1 = new LabelNode();
		list.add(l1);
		if(getASMVersion() < 5)
			list.add(new LineNumberNode(0, l1));//Force Labels to be created so JIT can do it's Job and optimize code
	}
	
	public static void insertLabelNode(InsnList list)
	{
		LabelNode l1 = new LabelNode();
		list.insert(l1);
		if(getASMVersion() < 5)
			list.insert(new LineNumberNode(0, l1));//Force Labels to be created so JIT can do it's Job and optimize code
	}
	
	public static void insertLabelNode(InsnList list, AbstractInsnNode spot)
	{
		InsnList l = new InsnList();
		LabelNode label = new LabelNode();
		l.add(label);
		if(getASMVersion() < 5)
			l.add(new LineNumberNode(0, label));//Force Labels to be created so JIT can do it's Job and optimize code
		list.insert(spot, l);
	}
	
	private static int ASM_VERSION = 0;
	public static int getASMVersion() 
	{
		if(ASM_VERSION > 0)
			return ASM_VERSION;
		ASM_VERSION = detectASMVersion();
		return ASM_VERSION;
	}

	private static int detectASMVersion() 
	{
		for(int i=5;i<=9;i++)
		{
			try 
			{
				if (Opcodes.class.getField("ASM" + i) != null)
					return i;
			} catch (Throwable ignored) {}
		}
		return 4;
	}
	
	//Start UTIL METHODS__________________________________________
	//____________________________________________________________
	
	/**
	 * Parse a Byte Safely
	 */
	public static byte parseByte(String value) 
	{
		return (byte) Long.parseLong(value, 10);
	}
	
	/**
	 * Parse a Short Safely
	 */
	public static short parseShort(String value) 
	{
		return (short) Long.parseLong(value, 10);
	}
	
	/**
	 * Parse a Int Safely
	 */
	public static int parseInt(String value) 
	{
		return (int) Long.parseLong(value, 10);
	}
	
	/**
	 * Parse a Int Safely
	 */
	public static long parseLong(String value) 
	{
		return Long.parseLong(value, 10);
	}
	
	/**
	 * Parse a Int Safely
	 */
	public static float parseFloat(String value) 
	{
		return Float.parseFloat(value);
	}
	
	public static double parseDouble(String value) 
	{
		return Double.parseDouble(value);
	}
	
	public static String[] splitFirst(String s, char delim)
	{
		int index = s.indexOf(delim);
		if(index == 0)
			index = s.indexOf(delim, 1);
		return index == -1 ? new String[]{s, ""} : new String[] {s.substring(0, index), s.substring(index + 1)};
	}
	
	//END UTIL METHODS___________________________________________________
	//___________________________________________________________________

}