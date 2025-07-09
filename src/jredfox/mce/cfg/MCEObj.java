package jredfox.mce.cfg;

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
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.ralleytn.simple.json.JSONArray;
import org.ralleytn.simple.json.JSONObject;

import jredfox.mce.ArrCache;
import jredfox.mce.ArrUtils;
import jredfox.mce.types.DataType;
import jredfox.mce.types.InsnTypes;
import jredfox.mce.util.MCECoreUtils;
import jredfox.mce.util.MCEUtil;
import jredfox.mce.util.OpcodeHelper;
import jredfox.mce.util.WildCardMatcher;

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
//		this.cached = null;//TODO fix
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

	public void configure(ClassNode cn)
	{	
		//Avoid GETFIELD OPCODES
		List<MCECached> cf = cached.get();
		List<MethodNode> ml = cn.methods;
		int size = ml.size();
		int index = 0;
		
		for(MethodNode m : ml)
		{
			boolean last = (index++ + 1) == size;
			List<MCECached> cache = new ArrayList(5);
			for(MCECached c : cf)
			{
				if(c.accept(cn, m))
					cache.add(c);
				else if(last)
					c.accepted = false;
			}
			
			for(MCECached c : cache)
			{
				c.apply();
				if(last)
					c.accepted = false;
			}
		}
	}

	public ThreadLocal<List<MCECached>> cached = new ThreadLocal()
	{
		@Override
		protected List<MCECached> initialValue() 
		{
			ArrayList<MCECached> l = new ArrayList(MCEObj.this.fields.size());
			List<MCEField> o = MCEObj.this.fields;
			for(MCEField f : o) {
				l.add(new MCECached(f));
			}
			l.trimToSize();
		    return l;
		}
	};

	public static void configure_old(String actualName, ClassNode classNode)
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
			FieldNode fn = MCECoreUtils.getFieldNode(f.name, classNode);
			
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
				System.err.println("Unsupported Type for Field:" + f.name + " desc:" + fn.desc + " in:" + mce.className);
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
					if(type.isWrapper && f.value != null)
						list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, type.clazz, "valueOf", type.descValueOf));
					list.add(new FieldInsnNode(Opcodes.PUTSTATIC, mce.classNameASM, f.name, fn.desc));
				}
				//static array support
				else
				{
					MCEArrField farr = (MCEArrField) f;
					list.add(new FieldInsnNode(Opcodes.GETSTATIC, mce.classNameASM, f.name, fn.desc));//arr_short
					if(farr.values.length < 2)
					{
						String val = farr.values[0];
						if(farr.index_start != farr.index_end)
						{
							//ArrUtils#fill(arr, v, index_start, index_end, increment); or ArrUtils#fill(arr, v, index_start, index_end);
							list.add(getNumInsn(val, type));//value
							list.add(getIntInsn(farr.index_start));//index_start
							list.add(getIntInsn(farr.index_end));//index_end
							if(type.hasIncrement)
								list.add(getIntInsn(farr.increment));//inecrement
							list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jredfox/mce/ArrUtils", "fill", type.getDescFill(val)));
						}
						else
						{
							//arr_short[index_start] = v;
							//or ArrUtils#set(arr, index, v);
							list.add(getIntInsn(farr.index_start));//set the index
							list.add(getNumInsn(val, type));//set the value
							if(farr.index_start > -1)
							{
								//convert the primitive datatype into it's object form before using AASTORE
								if(type.isWrapper && val != null)
									list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, type.clazz, "valueOf", type.descValueOf));
								list.add(new InsnNode(type.arrayStore));//stores the value
							}
							else
								list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jredfox/mce/ArrUtils", "set", type.getDescSet(val)));
						}
					}
					else
					{
						//ArrUtils#insert(arr, new arr[]{this.values}, farr.index_start);
						genStaticArraySafe(list, farr.values, type, farr.hasNULL);
						list.add(getIntInsn(farr.index_start));
						list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jredfox/mce/ArrUtils", "insert", type.getDescInsert(farr.hasNULL)));
					}
				}
				
				//Injection Point
				inject(classNode, m, list, f.inject);
			}
			
			//prevents memory leaks of arrays and allows the GC to clean it later
			f.gc();
		}
	}

	private static void inject(ClassNode classNode, MethodNode m, InsnList list, InsertionPoint in) 
	{
		if(in.type == InsnTypes.NULL)
		{
			if(in.opp == Opperation.AFTER)
			{
				addLabelNode(list);
				m.instructions.insert(MCECoreUtils.getLastReturn(m).getPrevious(), list);
			}
			else if(in.opp == Opperation.BEFORE)
			{
				insertLabelNode(list);
				if(m.name.equals("<init>"))
					m.instructions.insert(getFirstCtrInsn(classNode, m), list);
				else
					m.instructions.insert(list);
			}
			return;
		}
		
		AbstractInsnNode point = in.point;
		InsnTypes type = in.type;
		int occurance = in.occurrence;
		int shift = in.shift;
		ShiftTo shiftTo = in.shiftTo;
		
		int found = 0;
		int foundShift = 0;
		
		//Find the Injection Point
		AbstractInsnNode ab = m.instructions.getFirst();
		AbstractInsnNode inject = null;
		while(ab != null)
		{
			if(equals(type, ab, point))
			{
				inject = ab;
				if(found == occurance)
					break;
				found++;
			}
			ab = ab.getNext();
		}
		
		//Injection Point not Found!
		if(inject == null)
		{
			System.err.println("Error Failed to Inject Point Not Found in Bytecode! InsertionPoint:" + in);
			return;
		}
		
		//ShiftTo either [LINE, LABEL, EXACT]
		AbstractInsnNode spot = inject;
		AbstractInsnNode insnIndex = spot;
		boolean hasFoundShift = false;
		
		do
		{
			switch(shiftTo)
			{
				case LINE:
				{
					if(insnIndex instanceof LineNumberNode)
					{
						spot = insnIndex;
						if(foundShift == shift)
						{
							hasFoundShift = true;
							break;
						}
						foundShift++;
					}
				}
				break;
				case LABEL:
				{
					if(insnIndex instanceof LabelNode)
					{
						spot = insnIndex;
						if(foundShift == shift)
						{
							hasFoundShift = true;
							break;
						}
						foundShift++;
					}
				}
				break;
				
				case EXACT:
				{
					spot = insnIndex;
					if(foundShift == shift)
					{
						hasFoundShift = true;
						break;
					}
					foundShift++;
				}
				break;
					
				default:
					break;
			}
			insnIndex = (in.opp == Opperation.AFTER) ? (insnIndex.getNext()) : (insnIndex.getPrevious());
		}
		while(insnIndex != null && !hasFoundShift);
		
		//If inject == spot then We never shifted so we want to insertBefore if the opperation was before
		//If shiftTo is Exact use Exact indexes and always inject before. Index 0 = insertBefore exact insn, Index 1 = insertBefore of the previous instruction
		if(in.opp == Opperation.BEFORE && (inject == spot || shiftTo == ShiftTo.EXACT))
		{
			insertLabelNode(list);
			m.instructions.insertBefore(spot, list);
		}
		else
		{
			addLabelNode(list);
			m.instructions.insert(spot, list);
		}
	}

	public static boolean equals(InsnTypes type, AbstractInsnNode ab, AbstractInsnNode point) 
	{
		switch(type)
		{
			case LabelNode:
				return ab instanceof LabelNode;
			case FieldInsnNode:
				return ab instanceof FieldInsnNode && MCECoreUtils.equals((FieldInsnNode) ab, (FieldInsnNode) point);
			case MethodInsnNode:
				return ab instanceof MethodInsnNode && MCECoreUtils.equals((MethodInsnNode) ab, (MethodInsnNode) point);
			case InsnNode:
				return ab instanceof InsnNode && MCECoreUtils.equals((InsnNode) ab, (InsnNode) point);
			case IntInsnNode:
				return ab instanceof IntInsnNode && MCECoreUtils.equals((IntInsnNode) ab, (IntInsnNode) point);
			case VarInsnNode:
				return ab instanceof VarInsnNode && MCECoreUtils.equals((VarInsnNode) ab, (VarInsnNode) point);
			case JumpInsnNode:
				return ab instanceof JumpInsnNode && MCECoreUtils.equals((JumpInsnNode) ab, (JumpInsnNode) point);
			case TypeInsnNode:
				return ab instanceof TypeInsnNode && MCECoreUtils.equals((TypeInsnNode) ab, (TypeInsnNode) point);
			case LdcInsnNode:
				return ab instanceof LdcInsnNode && MCECoreUtils.equals((LdcInsnNode) ab, (LdcInsnNode) point);
			case Opcode:
				return MCECoreUtils.equalsOpcode(ab, point);

			default:
				break;
		}
		return false;
	}

	public static AbstractInsnNode getFirstCtrInsn(ClassNode cn, MethodNode m) 
	{
		AbstractInsnNode a = m.instructions.getFirst();
		while(a != null)
		{
			if(a.getOpcode() == Opcodes.INVOKESPECIAL && a instanceof MethodInsnNode)
			{
				MethodInsnNode am = (MethodInsnNode) a;
				if(am.name.equals("<init>") && (am.owner.equals(cn.name) || am.owner.equals(cn.superName)))
				{
					AbstractInsnNode nxt = nextRealInsn(am);
					
					//if the return instruction appears right after init in rare cases assume this is the last injection point
					if(nxt == null || MCECoreUtils.isReturnOpcode(nxt.getOpcode()))
						return am;
					
					AbstractInsnNode prev = prevRealInsn(am);
					int prevOpcode = prev != null ? prev.getOpcode() : 0;
					if(!OpcodeHelper.BAD_CTR_OPCODES.contains(prevOpcode))
					{
						return am;
					}
				}
			}
			a = a.getNext();//increment the index
		}
		return null;
	}

	public static AbstractInsnNode nextRealInsn(AbstractInsnNode a) 
	{
		do
		{
			a = a.getNext();
		}
		while (a != null && a.getOpcode() == -1);
		
		return a;
	}
	
	public static AbstractInsnNode prevRealInsn(AbstractInsnNode a) 
	{
		do
		{
			a = a.getPrevious();
		}
		while (a != null && a.getOpcode() == -1);
		
		return a;
	}

	/**
	 * Generates a Static Array Safely and if bigger then size of 10 will generate it now and cache it to prevent exceeding the bytecode limit
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
			if(str_v == null)
				continue;//SKIP NULL or 0 Values as it's already null / zero from creating the new array
			AbstractInsnNode valInsn = null;
			switch(type)
			{
				case WRAPPED_BOOLEAN:
				case BOOLEAN:
				{
					boolean v = MCEUtil.parseBoolean(str_v);
					if(!v && !isWrapper)
						continue;
					valInsn = getBoolInsn(v);
				}
				break;
				case WRAPPED_BYTE:
				case BYTE:
				{
					byte v = MCEUtil.parseByte(str_v);
					if(v == 0 && !isWrapper)
						continue;
					valInsn = getIntInsn(v);
				}
				break;
				case WRAPPED_SHORT:
				case SHORT:
				{
					short v = MCEUtil.parseShort(str_v);
					if(v == 0 && !isWrapper)
						continue;
					valInsn = getIntInsn(v);
				}
				break;
				case WRAPPED_INT:
				case INT:
				{
					int v = MCEUtil.parseInt(str_v);
					if(v == 0 && !isWrapper)
						continue;
					valInsn = getIntInsn(v);
				}
				break;
				case WRAPPED_LONG:
				case LONG:
				{
					long v = MCEUtil.parseLong(str_v);
					if(v == 0 && !isWrapper)
						continue;
					valInsn = getLongInsn(v);
				}
				break;
				case WRAPPED_FLOAT:
				case FLOAT:
				{
					float v = MCEUtil.parseFloat(str_v);
					if(v == 0 && !isWrapper)
						continue;
					valInsn = getFloatInsn(v);
				}
				break;
				case WRAPPED_DOUBLE:
				case DOUBLE:
				{
					double v = MCEUtil.parseDouble(str_v);
					if(v == 0 && !isWrapper)
						continue;
					valInsn = getDoubleInsn(v);
				}
				break;
				case STRING:
					valInsn = new LdcInsnNode(str_v);
				break;
				
				case WRAPPED_CHAR:
				case CHAR:
				{
					char v = MCEUtil.parseChar(str_v);
					if(v == (char)0 && !isWrapper)
						continue;
					valInsn = getIntInsn(v);
				}
				
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
		//NULL support
		if(str_v == null)
			return getNullInsn(type);
		
		switch(type)
		{
			case WRAPPED_BOOLEAN:
			case BOOLEAN:
				return getBoolInsn(MCEUtil.parseBoolean(str_v));
			case WRAPPED_BYTE:
			case BYTE:
				return getIntInsn(MCEUtil.parseByte(str_v));
			case WRAPPED_SHORT:
			case SHORT:
				return getIntInsn(MCEUtil.parseShort(str_v));
			case WRAPPED_INT:
			case INT:
				return getIntInsn(MCEUtil.parseInt(str_v));
			case WRAPPED_LONG:
			case LONG:
				return getLongInsn(MCEUtil.parseLong(str_v));
			case WRAPPED_FLOAT:
			case FLOAT:
				return getFloatInsn(MCEUtil.parseFloat(str_v));
			case WRAPPED_DOUBLE:
			case DOUBLE:
				return getDoubleInsn(MCEUtil.parseDouble(str_v));
			case STRING:
				return new LdcInsnNode(str_v);
			case WRAPPED_CHAR:
			case CHAR:
				return getIntInsn(MCEUtil.parseChar(str_v));
			default:
				break;
		}
		return null;
	}

	public static AbstractInsnNode getNullInsn(DataType type) 
	{
		if(type.isObject)
			return new InsnNode(Opcodes.ACONST_NULL);
		
		//Handle NULL Primitives
		switch(type)
		{
			case CHAR:
			case BOOLEAN:
			case BYTE:
			case SHORT:
			case INT:
				return getIntInsn(0);
			case LONG:
				return getLongInsn(0);
			case FLOAT:
				return getFloatInsn(0);
			case DOUBLE:
				return getDoubleInsn(0);

			default:
				return null;
		}
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
				if (!wc && !wcd)
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
																			: desc.equals("C") ? "char"
																		: desc.equals("Ljava/lang/Boolean;") ? "Boolean"
																: desc.equals("Ljava/lang/Byte;") ? "Byte"
															: desc.equals("Ljava/lang/Short;") ? "Short"
														: desc.equals("Ljava/lang/Integer;") ? "Integer"
												: desc.equals("Ljava/lang/Long;") ? "Long"
										: desc.equals("Ljava/lang/Float;") ? "Float"
								: desc.equals("Ljava/lang/Double;") ? "Double"
							: desc.equals("Ljava/lang/Character;") ? "Character"
						: UNSUPPORTED;
	}
	
	public static void addLabelNode(InsnList list)
	{
		LabelNode l1 = new LabelNode();
		list.add(l1);
		if(ASM_VERSION < 5)
			list.add(new LineNumberNode(0, l1));//Force Labels to be created so JIT can do it's Job and optimize code
	}
	
	public static void insertLabelNode(InsnList list)
	{
		LabelNode l1 = new LabelNode();
		if(ASM_VERSION < 5)
			list.insert(new LineNumberNode(0, l1));//Force Labels to be created so JIT can do it's Job and optimize code
		list.insert(l1);
	}
	
	public static void insertLabelNode(InsnList list, AbstractInsnNode spot)
	{
		InsnList l = new InsnList();
		LabelNode label = new LabelNode();
		l.add(label);
		if(ASM_VERSION < 5)
			l.add(new LineNumberNode(0, label));//Force Labels to be created so JIT can do it's Job and optimize code
		list.insert(spot, l);
	}
	
	private static int ASM_VERSION = detectASMVersion();

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

}