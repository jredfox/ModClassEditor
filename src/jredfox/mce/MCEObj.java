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

import jredfox.mce.MCEObj.InsertionPoint.Opperation;
import jredfox.mce.MCEObj.InsertionPoint.ShiftTo;
import jredfox.mce.tree.InsnTypes;
import jredfox.mce.tree.MCEIndexLabel;
import jredfox.mce.tree.MCEOpcode;

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
	
	public static class InsertionPoint
	{
		public static enum ShiftTo
		{
			LINE,
			LABEL,
			EXACT;
			
			public static ShiftTo get(String v)
			{
				v = v.trim().toUpperCase();
				
				if(v.startsWith("LABEL"))
					return LABEL;
				else if(v.startsWith("EXACT") || v.startsWith("INSN"))
					return EXACT;
				
				return LINE;
			}
		}
		
		public static enum Opperation {
			BEFORE,
			AFTER;
			
			public static Opperation get(String v){
				return v.equals("before") ? BEFORE : AFTER;
			}
		}
		
		/**
		 * Opperation must be "before" or "after"
		 */
		public Opperation opp = Opperation.AFTER;
		/**
		 * The ASM Injection Point when NON-NULL will be a specific injection point instead of before or after. The opp will combine with this
		 */
		public AbstractInsnNode point;
		/**
		 * The ASM Injection Point Cached Insn Type for easy switches later
		 */
		public InsnTypes type = InsnTypes.NULL;
		/**
		 * the index of the InjectionPoint's Occurance
		 */
		public int occurrence;
		/**
		 * Shift X Number of {@link #shiftTo} Instructions
		 */
		public int shift;
		/**
		 * The Insn Type we are shifting
		 */
		public ShiftTo shiftTo = ShiftTo.LINE;
		
		public InsertionPoint(JSONObject j)
		{
			Object o = j.containsKey("inject") ? j.get("inject") : j.get("insert");
			if(o instanceof JSONObject)
			{
				JSONObject oj = (JSONObject) o;
				this.parse(oj.getString("point"));
				if(this.type != InsnTypes.LabelNode)
					this.occurrence = parseInt(safeString(oj.getAsString("occurrence"), "0").replace("start", "0").replace("end", "-1"));
				this.shift =  parseInt(safeString(oj.getAsString("shift"), "0").replace("start", "0").replace("end", "-1"));
				if(oj.containsKey("shiftTo"))
					this.shiftTo = ShiftTo.get(oj.getAsString("shiftTo"));
			}
			else
				this.parse(safeString((String) o));
			
			if(this.point != null)
				System.out.println("DEBUG InsertionPoint " + this);
		}
		
		public InsertionPoint(String p)
		{
			this.parse(p);
		}

		protected void parse(String p) 
		{
			p = safeString(p, "after");
			String[] arr = p.split(",");
			if(arr.length == 0)
				return;
			
			String v0 = arr[0].trim().toLowerCase();
			int typeIndex = 0;
			String type;
			
			//If there is no ASM Injection point parse the opp and return
			if(arr.length == 1 && (v0.equals("before") || v0.equals("after")))
			{
				this.opp = Opperation.get(v0);
				return;
			}
			else if(v0.isEmpty())
			{
				if(arr.length > 1)
					System.err.println("Maulformed Line! Opperation or Type not Found: '" + p + "'");
				return;
			}
			
			if(v0.startsWith("before"))
			{
				this.opp = Opperation.BEFORE;
				if(v0.startsWith("before:"))
				{
					type = v0.substring(7).trim();
				}
				else
				{
					type = arr.length > 1 ? (arr[1].trim().toLowerCase()) : (v0);
					typeIndex++;
				}
			}
			else if(v0.startsWith("after"))
			{
				if(v0.startsWith("after:"))
				{
					type = v0.substring(6).trim();
				}
				else
				{
					type = arr.length > 1 ? (arr[1].trim().toLowerCase()) : (v0);
					typeIndex++;
				}
			}
			else
				type = v0;
			
			try
			{
				boolean nf = type.endsWith("node");
				if(nf && type.equals("insnnode") || type.equals("insn"))
				{
					this.point = new InsnNode(OpcodeHelper.getOppcode(arr[typeIndex + 1]));
					this.type = InsnTypes.InsnNode;
				}
				else if(nf && type.equals("intinsnnode") || type.equals("intinsn"))
				{
					int oc = OpcodeHelper.getOppcode(arr[typeIndex + 1]);
					int val = 0;
					//parse the val based on it's type. Insn supports Byte, Short except for when the Opcode NEWARRAY is called then it supports int
					switch(oc)
					{
						case Opcodes.BIPUSH:
							val = parseByte(arr[typeIndex + 2]);
						break;
						
						case Opcodes.SIPUSH:
							val = parseShort(arr[typeIndex + 2]);
						break;
						
						default:
							val = parseInt(arr[typeIndex + 2]);
						break;
					}
					this.point = new IntInsnNode(oc, val);
					this.type = InsnTypes.IntInsnNode;
				}
				else if(nf && type.equals("varinsnnode") || type.equals("varinsn"))
				{
					this.point = new VarInsnNode(OpcodeHelper.getOppcode(arr[typeIndex + 1]), parseInt(arr[typeIndex + 2]));
					this.type = InsnTypes.VarInsnNode;
				}
				else if(nf && type.equals("jumpinsnnode") || type.equals("jumpinsn"))
				{
					this.point = new JumpInsnNode(OpcodeHelper.getOppcode(arr[typeIndex + 1]), new LabelNode());
					this.type = InsnTypes.JumpInsnNode;
				}
				else if(nf && type.equals("typeinsnnode") || type.equals("typeinsn"))
				{
					this.point = new TypeInsnNode(OpcodeHelper.getOppcode(arr[typeIndex + 1]), parseString(arr[typeIndex + 2]));
					this.type = InsnTypes.TypeInsnNode;
				}
				else if(nf && type.equals("ldcinsnnode") || type.equals("ldcinsn"))
				{
					String ldc = p.substring(p.toLowerCase().indexOf("ldcinsnnode"));
					ldc = ldc.substring(ldc.indexOf(',') + 1).trim();
					
					//Handle EMPTY String
					if(ldc.isEmpty())
					{
						this.point = new LdcInsnNode("");
					}
					else
					{
						//They didn't specify string but it's expected to be a string
						if(ldc.charAt(0) == '"')
						{
							this.point = new LdcInsnNode(parseString(ldc));
						}
						else
						{
							String[] ldc_arr = splitFirst(ldc, ',');
							String ldc_type = ldc_arr[0].trim().toUpperCase();
							String ldc_val = parseString(ldc_arr[1]);
							if(ldc_type.equals("INT") || ldc_type.equals("INTEGER"))
								this.point = new LdcInsnNode(new Integer(parseInt(ldc_val)));
							else if(ldc_type.equals("LONG"))
								this.point = new LdcInsnNode(new Long(parseLong(ldc_val)));
							else if(ldc_type.equals("FLOAT"))
								this.point = new LdcInsnNode(new Float(parseFloat(ldc_val)));
							else if(ldc_type.equals("DOUBLE"))
								this.point = new LdcInsnNode(new Double(parseDouble(ldc_val)));
							else if(ldc_type.equals("STR") || ldc_type.equals("STRING"))
								this.point = new LdcInsnNode(ldc_val);
							else
								System.err.println("INVALID LdcInsnNode Type: '" + ldc_type + "'. Valid: [STRING, INTEGER, LONG, FLOAT, DOUBLE]");
						}
					}
					if(this.point != null)
						this.type = InsnTypes.LdcInsnNode;
				}
				else if(nf && type.equals("fieldinsnnode") || type.equals("fieldinsn"))
				{
					this.point = new FieldInsnNode(OpcodeHelper.getOppcode(arr[typeIndex + 1]), parseString(arr[typeIndex + 2]), parseString(arr[typeIndex + 3]), parseString(arr[typeIndex + 4]));
					this.type = InsnTypes.FieldInsnNode;
				}
				else if(nf && type.equals("methodinsnnode") || type.equals("methodinsn"))
				{
					this.point = new MethodInsnNode(OpcodeHelper.getOppcode(arr[typeIndex + 1]), parseString(arr[typeIndex + 2]), parseString(arr[typeIndex + 3]), parseString(arr[typeIndex + 4]));
					this.type = InsnTypes.MethodInsnNode;
				}
				else if(nf && type.equals("labelnode") || type.equals("label"))
				{
					int lindex = parseInt(arr[typeIndex + 1]);
					this.point = new MCEIndexLabel(lindex);
					this.occurrence = lindex;
					this.type = InsnTypes.LabelNode;
					this.shiftTo = ShiftTo.LABEL;
				}
				else if(type.startsWith("label:"))
				{
					int lindex = parseInt(type.substring(6));
					this.point = new MCEIndexLabel(lindex);
					this.occurrence = lindex;
					this.type = InsnTypes.LabelNode;
					this.shiftTo = ShiftTo.LABEL;
				}
				else if(type.equals("opcode"))
				{
					this.point = new MCEOpcode(OpcodeHelper.getOppcode(arr[typeIndex + 1]));
					this.type = InsnTypes.Opcode;
				}
				else
				{
					if(type.startsWith("line") || type.startsWith("label"))
						System.err.println("Missing ':' on Line or Label Injection Point! Line: '" + p + "'");
					else if(OpcodeHelper.hasOpcode(type))
						System.err.println("Invalid Injection Point String: '" + p + "'\nType is Missing! It Must be one of these Types:[MethodInsnNode, FieldInsnNode, InsnNode, Opcode, IntInsnNode, LdcInsnNode, VarInsnNode, TypeInsnNode, JumpInsnNode, LabelNode, LineNumberNode, LINE:<int>, LABEL:<int> ]");
					else
						System.err.println("Unsupported AbstractInsnNode for ASM Injection Point! \"" + type.toUpperCase() + "\"");
				}
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				System.err.println("Error while parsing Injection Point(Insertion Point):" + p);
				e.printStackTrace();
			}
		}
		
		@Override
		public String toString()
		{
			return this.opp + "," + this.point + ", Index:" + this.occurrence + ", shift:" + this.shift + ", shiftTo:" + this.shiftTo;
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
		public InsertionPoint inject;
		
		public MCEField()
		{
			
		}
		
		public MCEField(JSONObject json)
		{
			this(json.getString("name"), json.getAsStringN("value"), json.getString("type"), json.getString("method"), json.getString("desc"), new InsertionPoint(json));
		}
		
		public MCEField(String name, String value, String type, String method, String desc, InsertionPoint inject)
		{
			this.name = name.trim();
			this.value = value;
			this.type = safeString(type).trim();
			this.method = safeString(method, "<clinit>").trim();
			this.desc = safeString(desc).trim();
			this.inject = inject;
		}

		public void gc() {}
	}
	
	private static String safeString(String s)
	{
		return safeString(s, "");
	}
	
	private static String safeString(String s, String def)
	{
		return (s == null || s.isEmpty()) ? def : s;
	}
	
	public static class MCEArrField extends MCEField
	{
		/**
		 * The list of values going to be applied to the static array
		 */
		public String[] values;
		/**
		 * whether or not the values has a null value used to support wrapper objects and strings
		 */
		public boolean hasNULL;
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
			this(json.getString("name"), json.getJSONArray("values"), json.getString("type"), json.getString("method"), json.getString("desc"), new InsertionPoint(json), json.getAsString("index"), json.getAsString("increment"));
		}
		
		public MCEArrField(String name, List values, String type, String method, String desc, InsertionPoint inject, String index, String increment)
		{
			super(name, null, type, method, desc, inject);
			
			//process values into the String[] array
			if(values != null && !values.isEmpty())
			{
				this.values = new String[values.size()];
				for(int i=0;i<values.size();i++)
				{
					Object o = values.get(i);
					if(o != null)
						this.values[i] = String.valueOf(o);
					else
						this.hasNULL = true;
				}
			}
			else
				this.values = new String[] {""};
			
			//process index
			String[] arr = splitFirst(safeString(index, "0").replace("start", "0"), '-');
			String str_start = arr[0];
			String str_end = arr[1];
			this.index_start = str_start.equals("end") ? -1 : parseInt(str_start);
			this.index_end = str_end.isEmpty() ? this.index_start : (str_end.equals("end") ? -1 : parseInt(str_end));
			
			//process increment
			this.increment = parseInt(safeString(increment, "0"));
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
				m.instructions.insert(CoreUtils.getLastReturn(m).getPrevious(), list);
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

	private static boolean equals(InsnTypes type, AbstractInsnNode ab, AbstractInsnNode point) 
	{
		switch(type)
		{
			case LabelNode:
				return ab instanceof LabelNode;
			case FieldInsnNode:
				return ab instanceof FieldInsnNode && CoreUtils.equals((FieldInsnNode) ab, (FieldInsnNode) point);
			case MethodInsnNode:
				return ab instanceof MethodInsnNode && CoreUtils.equals((MethodInsnNode) ab, (MethodInsnNode) point);
			case InsnNode:
				return ab instanceof InsnNode && CoreUtils.equals((InsnNode) ab, (InsnNode) point);
			case IntInsnNode:
				return ab instanceof IntInsnNode && CoreUtils.equals((IntInsnNode) ab, (IntInsnNode) point);
			case VarInsnNode:
				return ab instanceof VarInsnNode && CoreUtils.equals((VarInsnNode) ab, (VarInsnNode) point);
			case JumpInsnNode:
				return ab instanceof JumpInsnNode && CoreUtils.equals((JumpInsnNode) ab, (JumpInsnNode) point);
			case TypeInsnNode:
				return ab instanceof TypeInsnNode && CoreUtils.equals((TypeInsnNode) ab, (TypeInsnNode) point);
			case LdcInsnNode:
				return ab instanceof LdcInsnNode && CoreUtils.equals((LdcInsnNode) ab, (LdcInsnNode) point);
			case Opcode:
				return CoreUtils.equalsOpcode(ab, point);

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
					if(nxt == null || CoreUtils.isReturnOpcode(nxt.getOpcode()))
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
					boolean v = parseBoolean(str_v);
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
				
				case WRAPPED_CHAR:
				case CHAR:
				{
					char v = parseChar(str_v);
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
				return getBoolInsn(parseBoolean(str_v));
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
			case WRAPPED_CHAR:
			case CHAR:
				return getIntInsn(parseChar(str_v));
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
		if(getASMVersion() < 5)
			list.add(new LineNumberNode(0, l1));//Force Labels to be created so JIT can do it's Job and optimize code
	}
	
	public static void insertLabelNode(InsnList list)
	{
		LabelNode l1 = new LabelNode();
		if(getASMVersion() < 5)
			list.insert(new LineNumberNode(0, l1));//Force Labels to be created so JIT can do it's Job and optimize code
		list.insert(l1);
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
	 * Parse a char Safely
	 */
	public static char parseChar(String value) 
	{
		if(value == null) return 0;
		
		try
		{
			return (char) Long.parseLong(value, 10);
		}
		catch(NumberFormatException num)
		{
			return value.isEmpty() ? ((char) 0) : value.charAt(0);
		}
	}
	
	/**
	 * parses a boolean allowing for 0 to be false and 1 to be true
	 * if string is null or empty it's returning false which is equal to 0 which is also what null converted to a primative is
	 */
	public static boolean parseBoolean(String s)
	{
		if(s == null)
			return false;
        int l = s.length();
        
        //if string is empty return false
        if(l == 0)
        	return false;
        
        //start left side trim if required
        int i = 0;
        while ((i < l) && (s.charAt(i) <= ' ')) {
            i++;
        }
        
        //if string is empty after trim return false
        if(i == l)
        	return false;
        
        char c = s.charAt(i);
        return c == 't' || c == 'T' || c == '1';
	}
	
	/**
	 * Parse a Byte Safely
	 */
	public static byte parseByte(String value) 
	{
		if(value == null) return 0;
		
		try
		{
			return (byte) Long.parseLong(value.trim(), 10);
		}
		catch(NumberFormatException e)
		{
			return 0;
		}
	}
	
	/**
	 * Parse a Short Safely
	 */
	public static short parseShort(String value) 
	{
		if(value == null) return 0;
		
		try
		{
			return (short) Long.parseLong(value.trim(), 10);
		}
		catch(NumberFormatException e)
		{
			return 0;
		}
	}
	
	/**
	 * Parse a Int Safely
	 */
	public static int parseInt(String value) 
	{
		if(value == null) return 0;
		
		try
		{
			return (int) Long.parseLong(value.trim(), 10);
		}
		catch(NumberFormatException e)
		{
			return 0;
		}
	}
	
	/**
	 * Parse a Int Safely
	 */
	public static long parseLong(String value) 
	{
		if(value == null) return 0;
		
		try
		{
			return Long.parseLong(value.trim(), 10);
		}
		catch(NumberFormatException e)
		{
			return 0;
		}
	}
	
	/**
	 * Parse a Int Safely
	 */
	public static float parseFloat(String value) 
	{
		if(value == null) return 0;
		
		try
		{
			return Float.parseFloat(value.trim());
		}
		catch(NumberFormatException e)
		{
			return 0;
		}
	}
	
	public static double parseDouble(String value) 
	{
		if(value == null) return 0;
		
		try
		{
			return Double.parseDouble(value.trim());
		}
		catch(NumberFormatException e)
		{
			return 0;
		}
	}
	
	public static String parseString(String value)
	{
		if(value == null)
			return "";
		
		value = value.trim();
		if(!value.isEmpty() && value.charAt(0) == '"')
		{
			int len = value.length();
			if (len == 1)
				return "";
			
			int end = len - 1;
			if (value.charAt(end) != '"')
				end++;
			return value.substring(1, end);
		}
		
		return value;
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