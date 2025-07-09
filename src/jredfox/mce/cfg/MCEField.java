package jredfox.mce.cfg;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.ralleytn.simple.json.JSONObject;

import jredfox.mce.types.DataType;
import jredfox.mce.types.InsnTypes;
import jredfox.mce.util.MCECoreUtils;
import jredfox.mce.util.MCEUtil;

public class MCEField
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
		this.type = MCEUtil.safeString(type).trim();
		this.method = MCEUtil.safeString(method, "<clinit>").trim();
		this.desc = MCEUtil.safeString(desc).trim();
		this.inject = inject;
	}

	public void gc() {}

	public void apply(ClassNode cn, MethodNode m, CachedInsertionPoint cip)
	{
		String str_type = this.type;
		FieldNode fn = MCECoreUtils.getFieldNode(this.name, cn);
		
		//Populate the Type
		if(str_type.isEmpty())
			str_type = MCEObj.getType(fn.desc);
		
		//Convert the type into a useable thing
		boolean  isArr = str_type.startsWith("[");
		DataType type = DataType.getType(isArr ? str_type.replace("[", "") : str_type);
		
		InsnList list = new InsnList();
		if(!isArr)
		{
			list.add(MCEObj.getNumInsn(this.value, type));
			if(type.isWrapper && this.value != null)
				list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, type.clazz, "valueOf", type.descValueOf));
			list.add(new FieldInsnNode(Opcodes.PUTSTATIC, cn.name, this.name, fn.desc));
		}
		
		//Inject the code
		this.inject(m, list, cip);
		
		//Prevent Memory Leaks
		this.gc();
	}
	
	private void inject(MethodNode m, InsnList list, CachedInsertionPoint cip) 
	{
		if(cip.opp == Opperation.AFTER)
		{
//			MCEObj.addLabelNode(list);
			m.instructions.insert(cip.point, list);
		}
		else if(cip.typeNormal && cip.point == null)
		{
//			MCEObj.insertLabelNode(list);
			m.instructions.insert(list);
		}
		else if(cip.opp == Opperation.BEFORE)
		{
//			MCEObj.insertLabelNode(list);
			m.instructions.insertBefore(cip.point, list);
		}
	}

	public CachedInsertionPoint capture(ClassNode cn, MethodNode m) 
	{
		InsertionPoint in = this.inject;
		if(in.type == InsnTypes.NULL)
		{
			if(in.opp == Opperation.AFTER)
			{
				return new CachedInsertionPoint(MCECoreUtils.getLastReturn(m).getPrevious(), Opperation.AFTER, true);
			}
			else if(in.opp == Opperation.BEFORE)
			{
				if(m.name.equals("<init>"))
					return new CachedInsertionPoint(MCEObj.getFirstCtrInsn(cn, m), Opperation.AFTER, true);
				else
					return new CachedInsertionPoint(null, Opperation.BEFORE, true);
			}
			return null;
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
			if(MCEObj.equals(type, ab, point))
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
			return null;
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
			return new CachedInsertionPoint(spot, Opperation.BEFORE, false);
		}
		else
		{
			return new CachedInsertionPoint(spot, Opperation.AFTER, false);
		}
	}

	public boolean canEdit(ClassNode c)
	{
		FieldNode fn = MCECoreUtils.getFieldNode(this.name, c);
		
		//Sanity Checks
		if(fn == null)
		{
			System.err.println("Field not Found:" + this.name + " in: " + c.name);
			return false;
		}
		else if((fn.access & Opcodes.ACC_STATIC) == 0)
		{
			System.err.println("Object Fields are not Supported for Editing yet as it's more complex and Per Object to Edit:" + this.name + " in:" + c.name);
			return false;
		}
		
		//Populate the Type
		String str_type = MCEObj.getType(fn.desc);
		
		//Convert the type into a useable thing
		boolean  isArr = str_type.startsWith("[");
		DataType type = DataType.getType(isArr ? str_type.replace("[", "") : str_type);
		
		//disallow unsupported field opperations to prevent runtime crashing
		if(type == DataType.NULL)
		{
			System.err.println("Unsupported Type for Field:" + this.name + " desc:" + fn.desc + " in:" + c.name);
			return false;
		}
		
		return true;
	}
	
	public void apply(ClassNode cn, AnnotationNode ann, CachedInsertionPoint cip)
	{
		
	}
}