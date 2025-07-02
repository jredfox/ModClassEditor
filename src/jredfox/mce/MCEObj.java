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
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.ralleytn.simple.json.JSONArray;
import org.ralleytn.simple.json.JSONObject;

import jredfox.mce.ArrUtils.Type;

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
			List<MethodNode> methodList = getMethodNodes(classNode, f.method, f.desc);
			if(methodList.isEmpty())
			{
				System.err.println("Method Not Found:" + f.method + " desc:\"" + f.desc + "\"");
				continue;
			}
			
			for(MethodNode m : methodList)
			{
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
				else if(type.equalsIgnoreCase("short"))
				{
					short s = parseShort(f.value);
					InsnNode insn = getConstantInsn(s);
					if(insn != null)
						list.add(insn);
					else if (s >= Byte.MIN_VALUE && s <= Byte.MAX_VALUE)
						list.add(new IntInsnNode(Opcodes.BIPUSH, s));
					else
						list.add(new IntInsnNode(Opcodes.SIPUSH, s));
					
					if(isWrapper)
						list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;"));
					
					list.add(new FieldInsnNode(Opcodes.PUTSTATIC, mce.classNameASM, f.name, (isWrapper ? "Ljava/lang/Short;" : "S") ));
				}
				else if(type.equals("int") || type.equals("Integer"))
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
					
					if(isWrapper)
						list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;"));
					
					list.add(new FieldInsnNode(Opcodes.PUTSTATIC, mce.classNameASM, f.name, (isWrapper ? "Ljava/lang/Integer;" : "I") ));
				}
				else if(type.equalsIgnoreCase("long"))
				{
					long v = parseLong(f.value);
					InsnNode insn = getConstantInsn(v);
					if(insn != null)
						list.add(insn);
					else
						list.add(new LdcInsnNode(new Long(v)));
					
					if(isWrapper)
						list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;"));
					
					list.add(new FieldInsnNode(Opcodes.PUTSTATIC, mce.classNameASM, f.name, (isWrapper ? "Ljava/lang/Long;" : "J") ));
				}
				else if(type.equalsIgnoreCase("float"))
				{
					float v = parseFloat(f.value);
					InsnNode insn = getConstantInsn(v);
					if(insn != null)
						list.add(insn);
					else
						list.add(new LdcInsnNode(new Float(v)));
					
					if(isWrapper)
						list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;"));
					
					list.add(new FieldInsnNode(Opcodes.PUTSTATIC, mce.classNameASM, f.name, (isWrapper ? "Ljava/lang/Float;" : "F") ));
				}
				else if(type.equalsIgnoreCase("double"))
				{
					double v = Double.parseDouble(f.value);
					InsnNode insn = getConstantInsn(v);
					if(insn != null)
						list.add(insn);
					else
						list.add(new LdcInsnNode(new Double(v)));
					
					if(isWrapper)
						list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;"));
					
					list.add(new FieldInsnNode(Opcodes.PUTSTATIC, mce.classNameASM, f.name, (isWrapper ? "Ljava/lang/Double;" : "D") ));
				}
				else if(type.equals("string"))
				{
					list.add(new LdcInsnNode(f.value));
					list.add(new FieldInsnNode(Opcodes.PUTSTATIC, mce.classNameASM, f.name, "Ljava/lang/String;"));
				}
				//static array support
				else if(type.startsWith("["))
				{
					MCEArrField farr = (MCEArrField) f;
					String atype = type.replace("[", "");
					ArrUtils.Type arr_type = ArrUtils.getType(atype);
					
					int store = Opcodes.BASTORE;
					String desc_fill = null;
					String desc_set = null;
					String desc_insert = null;
					boolean hasIncrem = true;
					PairStr wrapperInsn = null;
					switch(arr_type)
					{
						case BOOLEAN:
							store = Opcodes.BASTORE;
							desc_fill = "([ZZII)V";
							desc_set = "([ZIZ)V";
							desc_insert = "([Z[ZI)V";
							hasIncrem = false;
						break;
						case BYTE:
							store = Opcodes.BASTORE;
							desc_fill =   "([BBIII)V";
							desc_set =    "([BIB)V";
							desc_insert = "([B[BI)V";
						break;
						case SHORT:
							store = Opcodes.SASTORE;
							desc_fill =   "([SSIII)V";
							desc_set =    "([SIS)V";
							desc_insert = "([S[SI)V";
						break;
						case INT:
							store = Opcodes.IASTORE;
							desc_fill =   "([IIIII)V";
							desc_set =    "([III)V";
							desc_insert = "([I[II)V";
						break;
						case LONG:
							store = Opcodes.LASTORE;
							desc_fill =   "([JJIII)V";
							desc_set =    "([JIJ)V";
							desc_insert = "([J[JI)V";
						break;
						case FLOAT:
							store = Opcodes.FASTORE;
							desc_fill =   "([FFIII)V";
							desc_set =    "([FIF)V";
							desc_insert = "([F[FI)V";
						break;
						case DOUBLE:
							store = Opcodes.DASTORE;
							desc_fill =   "([DDIII)V";
							desc_set =    "([DID)V";
							desc_insert = "([D[DI)V";
						break;
						case STRING:
							store = Opcodes.AASTORE;
							desc_fill =   "([Ljava/lang/String;Ljava/lang/String;II)V";
							desc_set =    "([Ljava/lang/String;ILjava/lang/String;)V";
							desc_insert = "([Ljava/lang/String;[Ljava/lang/String;I)V";
							hasIncrem = false;
						break;
						case WRAPPED_BOOLEAN:
							store = Opcodes.AASTORE;
							desc_fill = "([Ljava/lang/Boolean;ZII)V";
							desc_set = "([Ljava/lang/Boolean;IZ)V";
							desc_insert = "([Ljava/lang/Boolean;[Ljava/lang/Boolean;I)V";
							wrapperInsn = new PairStr("java/lang/Boolean", "(Z)Ljava/lang/Boolean;");
							hasIncrem = false;
						break;
						case WRAPPED_BYTE:
							store = Opcodes.AASTORE;
							desc_fill = "([Ljava/lang/Byte;BIII)V";
							desc_set = "([Ljava/lang/Byte;IB)V";
							desc_insert = "([Ljava/lang/Byte;[Ljava/lang/Byte;I)V";
							wrapperInsn = new PairStr("java/lang/Byte", "(B)Ljava/lang/Byte;");
						break;
						case WRAPPED_SHORT:
							store = Opcodes.AASTORE;
							desc_fill = "([Ljava/lang/Short;SIII)V";
							desc_set = "([Ljava/lang/Short;IS)V";
							desc_insert = "([Ljava/lang/Short;[Ljava/lang/Short;I)V";
							wrapperInsn = new PairStr("java/lang/Short", "(S)Ljava/lang/Short;");
						break;
						case WRAPPED_INT:
							store = Opcodes.AASTORE;
							desc_fill = "([Ljava/lang/Integer;IIII)V";
							desc_set = "([Ljava/lang/Integer;II)V";
							desc_insert = "([Ljava/lang/Integer;[Ljava/lang/Integer;I)V";
							wrapperInsn = new PairStr("java/lang/Integer", "(I)Ljava/lang/Integer;");
						break;
						case WRAPPED_LONG:
							store = Opcodes.AASTORE;
							desc_fill = "([Ljava/lang/Long;JIII)V";
							desc_set = "([Ljava/lang/Long;IJ)V";
							desc_insert = "([Ljava/lang/Long;[Ljava/lang/Long;I)V";
							wrapperInsn = new PairStr("java/lang/Long", "(J)Ljava/lang/Long;");
						break;
						case WRAPPED_FLOAT:
							store = Opcodes.AASTORE;
							desc_fill = "([Ljava/lang/Float;FIII)V";
							desc_set = "([Ljava/lang/Float;IF)V";
							desc_insert = "([Ljava/lang/Float;[Ljava/lang/Float;I)V";
							wrapperInsn = new PairStr("java/lang/Float", "(F)Ljava/lang/Float;");
						break;
						case WRAPPED_DOUBLE:
							store = Opcodes.AASTORE;
							desc_fill = "([Ljava/lang/Double;DIII)V";
							desc_set = "([Ljava/lang/Double;ID)V";
							desc_insert = "([Ljava/lang/Double;[Ljava/lang/Double;I)V";
							wrapperInsn = new PairStr("java/lang/Double", "(D)Ljava/lang/Double;");
						break;
						default:
							break;
					}
					list.add(new FieldInsnNode(Opcodes.GETSTATIC, mce.classNameASM, f.name, fn.desc));//arr_short
					if(farr.values.length < 2)
					{
						if(farr.index_start != farr.index_end)
						{
							//ArrUtils#fill(arr, v, index_start, index_end, increment); or ArrUtils#fill(arr, v, index_start, index_end);
							list.add(getNumInsn(farr.values[0], arr_type));//value
							list.add(getIntInsn(farr.index_start));//index_start
							list.add(getIntInsn(farr.index_end));//index_end
							if(hasIncrem)
								list.add(getIntInsn(farr.increment));//inecrement
							list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jredfox/mce/ArrUtils", "fill", desc_fill));
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
								if(wrapperInsn != null)
									list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, wrapperInsn.x, "valueOf", wrapperInsn.y));
								list.add(new InsnNode(store));//stores the value
							}
							else
								list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jredfox/mce/ArrUtils", "set", desc_set));
						}
					}
					else
					{
						//ArrUtils#insert(arr, new arr[]{this.values}, farr.index_start);
						genStaticArray(list, farr.values, arr_type);
						list.add(getIntInsn(farr.index_start));
						list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jredfox/mce/ArrUtils", "insert", desc_insert));
					}
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
	}

	/**
	 * Generates a static array initialized with specified values
	 * @param list the list to generate the bytecode into list#add will be called so keep that in mind no injections points will be present
	 * @param values a string list of values representing any data type and will get parsed based on the type param
	 * @param type defines what data type of static array to create
	 * @WARNING: DO NOT USE past 10 indexes as Java's max bytecode limit per method is 65535 bytes. This method works but is depreciated please use the ArrUtils#gen instead
	 */
	@Deprecated
	public static void genStaticArray(InsnList list, String[] values, Type type) 
	{
		//array size
		list.add(getIntInsn(values.length));
		
		//array type
		int arrNew = 0;
		int store = 0;
		String arrNewObj = null;
		String desc_wrapper = null;
		switch(type)
		{
			case BOOLEAN:
				arrNew = Opcodes.T_BOOLEAN;
				store = Opcodes.BASTORE;
			break;
			case BYTE:
				arrNew = Opcodes.T_BYTE;
				store = Opcodes.BASTORE;
			break;
			case SHORT:
				arrNew = Opcodes.T_SHORT;
				store = Opcodes.SASTORE;
			break;
			case INT:
				arrNew = Opcodes.T_INT;
				store = Opcodes.IASTORE;
			break;
			case LONG:
				arrNew = Opcodes.T_LONG;
				store = Opcodes.LASTORE;
			break;
			case FLOAT:
				arrNew = Opcodes.T_FLOAT;
				store = Opcodes.FASTORE;
			break;
			case DOUBLE:
				arrNew = Opcodes.T_DOUBLE;
				store = Opcodes.DASTORE;
			break;
			case STRING:
				arrNewObj = "java/lang/String";
				store = Opcodes.AASTORE;
			break;
			case WRAPPED_BOOLEAN:
				arrNewObj = "java/lang/Boolean";
				store = Opcodes.AASTORE;
				desc_wrapper = "(Z)Ljava/lang/Boolean;";
			break;
			case WRAPPED_BYTE:
				arrNewObj = "java/lang/Byte";
				store = Opcodes.AASTORE;
				desc_wrapper = "(B)Ljava/lang/Byte;";
			break;
			case WRAPPED_SHORT:
				arrNewObj = "java/lang/Short";
				store = Opcodes.AASTORE;
				desc_wrapper = "(S)Ljava/lang/Short;";
			break;
			case WRAPPED_INT:
				arrNewObj = "java/lang/Integer";
				store = Opcodes.AASTORE;
				desc_wrapper = "(I)Ljava/lang/Integer;";
			break;
			case WRAPPED_LONG:
				arrNewObj = "java/lang/Long";
				store = Opcodes.AASTORE;
				desc_wrapper = "(J)Ljava/lang/Long;";
			break;
			case WRAPPED_FLOAT:
				arrNewObj = "java/lang/Float";
				store = Opcodes.AASTORE;
				desc_wrapper = "(F)Ljava/lang/Float;";
			break;
			case WRAPPED_DOUBLE:
				arrNewObj = "java/lang/Double";
				store = Opcodes.AASTORE;
				desc_wrapper = "(D)Ljava/lang/Double;";
			break;

			default:
				throw new RuntimeException("Unsupported Type:" + type);
		}
		boolean isWrapper = desc_wrapper != null;
		if(arrNewObj == null)
			list.add(new IntInsnNode(Opcodes.NEWARRAY, arrNew));
		else
			list.add(new TypeInsnNode(Opcodes.ANEWARRAY, arrNewObj));
		
		//initialize NON-ZERO VALUES
		for(int index=0; index < values.length; index++)
		{
			String str_v = values[index];
			AbstractInsnNode valInsn = null;
			int v_int = 0;
			long v_l = 0;
			float v_f = 0;
			double v_d = 0;
			String v_str = "";
			switch(type)
			{
				case WRAPPED_BOOLEAN:
				case BOOLEAN:
					boolean v_bool = Boolean.parseBoolean(str_v);
					if(!v_bool && !isWrapper)
						continue;
					valInsn = getBoolInsn(v_bool);
				break;
				case WRAPPED_BYTE:
				case BYTE:
					v_int = parseByte(str_v);
					if(v_int == 0 && !isWrapper)
						continue;
					valInsn = getIntInsn(v_int);
				break;
				case WRAPPED_SHORT:
				case SHORT:
					v_int = parseShort(str_v);
					if(v_int == 0 && !isWrapper)
						continue;
					valInsn = getIntInsn(v_int);
				break;
				case WRAPPED_INT:
				case INT:
					v_int = parseInt(str_v);
					if(v_int == 0 && !isWrapper)
						continue;
					valInsn = getIntInsn(v_int);
				break;
				case WRAPPED_LONG:
				case LONG:
					v_l = parseLong(str_v);
					if(v_l == 0 && !isWrapper)
						continue;
					valInsn = getLongInsn(v_l);
				break;
				case WRAPPED_FLOAT:
				case FLOAT:
					v_f = parseFloat(str_v);
					if(v_f == 0 && !isWrapper)
						continue;
					valInsn = getFloatInsn(v_f);
				break;
				case WRAPPED_DOUBLE:
				case DOUBLE:
					v_d = parseDouble(str_v);
					if(v_d == 0 && !isWrapper)
						continue;
					valInsn = getDoubleInsn(v_d);
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
				list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, arrNewObj, "valueOf", desc_wrapper));//converts the datatype into it's Object form
			list.add(new InsnNode(store));
		}
	}
	
	public static AbstractInsnNode getNumInsn(String str_v, ArrUtils.Type type)
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
			case LONG:
			case WRAPPED_LONG:
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
	
	private static double parseDouble(String value) 
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