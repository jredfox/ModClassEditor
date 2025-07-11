package jredfox.mce.cfg;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.ralleytn.simple.json.JSONObject;

import jredfox.mce.Transformer;
import jredfox.mce.types.DataType;
import jredfox.mce.types.InsnTypes;
import jredfox.mce.util.MCECoreUtils;
import jredfox.mce.util.MCEUtil;
import jredfox.mce.util.WildCardMatcher;

public class MCEField
{
	/**
	 * The Transformed & Deobfuscated Class Name that is the Owner of the Field
	 */
	public String owner;
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
	/**
	 * cached method name has a wildcard
	 */
	public boolean wc;
	/**
	 * cached method desc has a wildcard
	 */
	public boolean wcd;
	/**
	 * cached method desc is empty
	 */
	public boolean mt;
	/**
	 * cached boolean for if we accept more then one method
	 */
	public boolean onlyOne;
	/**
	 * weather or not we accepted a MethodNode already
	 */
	public boolean accepted;
	/**
	 * Occurs when a MCEField cannot edit a class due to FieldNode Desc Mismatch
	 */
	public boolean err;
	/**
	 * When True we have checked if an MCEField {@link #canEdit(ClassNode)} the class
	 */
	public boolean chkErr;
	/**
	 * The real Cached Insertion Point not a Configuration Object like InsertionPoint!
	 */
	public CachedInsertionPoint cip;
	/**
	 * The cached ClassNode that comes from {@link #accept(MethodNode)} or {@link #accept(AnnotationNode)}
	 */
	public ClassNode ccn;
	/**
	 * The cached MethodNode that comes from {@link #accept(MethodNode)}
	 */
	public MethodNode cmn;
	
	public MCEField()
	{
		
	}
		
	public MCEField(MCEObj parent, JSONObject json)
	{
		this(parent.classNameASM, json.getString("name"), json.getAsStringN("value"), json.getString("type"), json.getString("method"), json.getString("desc"), new InsertionPoint(json));
	}
		
	public MCEField(String owner, String name, String value, String type, String method, String desc, InsertionPoint inject)
	{
		this.owner = owner;
		this.name = name.trim();
		this.value = value;
		this.type = MCEUtil.safeString(type).trim();
		this.method = MCEUtil.safeString(method, "<clinit>").trim();
		this.desc = MCEUtil.safeString(desc).trim();
		this.inject = inject;
		//cache frequently used booleans
		this.wc =  MCEUtil.isWildCard(this.method);
		this.wcd = MCEUtil.isWildCard(this.desc);
		this.mt = this.desc.isEmpty();
		this.onlyOne = !this.wc && !this.wcd;
		
		System.out.println("DEBUG:" + this.owner + " " + this.name + " " + this.method + " " + this.inject);
	}
	
	public boolean accept(ClassNode cn, MethodNode m)
	{
		if(this.err || this.accepted && this.onlyOne)
			return false;
		else if(!this.chkErr && !this.canEdit(cn))
		{
			this.err = true;
			return false;
		}
		
		if ((this.wc ? WildCardMatcher.match(m.name, this.method, true) : m.name.equals(this.method))
				&& (this.mt || (this.wcd ? WildCardMatcher.match(m.desc, this.desc, true)
						: m.desc.equals(this.desc)))) 
		{
			this.cip = this.capture(cn, m);
			if(this.cip == null)
				return false;
			
			this.ccn = cn;
			this.cmn = m;
			this.accepted = true;
			return true;
		}
		
		return false;
	}
	
	/**
	 * Clears the Cached Data from Working on a Method / AnnotationNode
	 * Calls {@link #gc()} at the end of the method call
	 */
	public void clear() 
	{
		this.accepted = false;
		this.err = false;
		this.chkErr = false;
		this.cip = null;
		this.ccn = null;
		this.cmn = null;
		this.cfn = null;
		this.cisArr = false;
		this.cdt = null;
		if(Transformer.gc)
			this.gc();
	}
	
	public void apply()
	{
		if(!this.accepted)
			return;
		
		this.apply(this.ccn, this.cmn, this.cip);
	}

	public void apply(ClassNode cn, MethodNode m, CachedInsertionPoint p)
	{
		if(this.cisArr)
			return;
		
		System.out.println("Applying:" + this.ccn + " " + this.cmn + " " + this.cip);
		FieldNode fn = this.cfn;
		DataType type = this.cdt;
		
		InsnList list = new InsnList();
		list.add(MCECoreUtils.getNumInsn(this.value, type));
		if(type.isWrapper && this.value != null)
			list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, type.clazz, "valueOf", type.descValueOf));
		list.add(new FieldInsnNode(Opcodes.PUTSTATIC, this.owner, this.name, fn.desc));
		
		//Inject the code
		this.inject(m, list, p);
	}
	
	public void apply(ClassNode cn, AnnotationNode ann, CachedInsertionPoint cip)
	{
		
	}
	
	private static boolean labelDisabled = false;
	public void applyLabel()
	{
		if(cip.firstInsn == null || labelDisabled)
			return;
		
		InsnList l = new InsnList();
		LabelNode l0 = new LabelNode();
		l.add(l0);
		l.add(new LineNumberNode(0, l0));
		
		AbstractInsnNode prev = MCECoreUtils.prevSkipFrames(this.cip.firstInsn);
		AbstractInsnNode next = MCECoreUtils.nextSkipFrames(this.cip.lastInsn);
		if(this.cip.labelBefore)
			this.cmn.instructions.insertBefore(this.cip.firstInsn, l);
		else
			this.cmn.instructions.insert(this.cip.lastInsn, l);
	}

	public void inject(MethodNode m, InsnList list, CachedInsertionPoint cip) 
	{
		if(Transformer.label)
		{
			cip.firstInsn = list.getFirst();
			cip.lastInsn = list.getLast();
		}
		
		if(cip.opp == Opperation.AFTER)
		{
			m.instructions.insert(cip.point, list);
		}
		else if(cip.typeNormal && cip.point == null)
		{
			m.instructions.insert(list);
		}
		else if(cip.opp == Opperation.BEFORE)
		{
			m.instructions.insertBefore(cip.point, list);
		}
	}

	/**
	 * Captures the Real Injection Point with the actual Opperation & type
	 */
	public CachedInsertionPoint capture(ClassNode cn, MethodNode m) 
	{
		InsertionPoint in = this.inject;
		if(in.type == InsnTypes.NULL)
		{
			if(in.opp == Opperation.AFTER)
			{
				return new CachedInsertionPoint(MCECoreUtils.getLastReturn(m).getPrevious(), Opperation.AFTER, true, false);
			}
			else if(in.opp == Opperation.BEFORE)
			{
				if(m.name.equals("<init>"))
					return new CachedInsertionPoint(MCECoreUtils.getFirstCtrInsn(cn, m), Opperation.AFTER, true, true);
				else
					return new CachedInsertionPoint(null, Opperation.BEFORE, true, true);
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
			if(MCECoreUtils.equals(type, ab, point))
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
							//Shift to injecting to after the LineNumberNode for AFTER as it's alot safer but only if it's the next insn
							if(in.opp == Opperation.AFTER && spot.getNext() instanceof LineNumberNode)
								spot = spot.getNext();
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
			return new CachedInsertionPoint(spot, Opperation.BEFORE, false, false);
		}
		else
		{
			return new CachedInsertionPoint(spot, Opperation.AFTER, false, (shiftTo == ShiftTo.EXACT));
		}
	}
	
	protected FieldNode cfn;
	protected boolean cisArr;
	protected DataType cdt;
	public boolean canEdit(ClassNode c)
	{
		this.chkErr = true;
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
		this.cfn = fn;
		
		String str_type = MCECoreUtils.getType(fn.desc);
		boolean  isArr = str_type.startsWith("[");
		DataType type = DataType.getType(isArr ? str_type.replace("[", "") : str_type);
		
		//disallow unsupported field opperations to prevent runtime crashing
		if(type == DataType.NULL)
		{
			System.err.println("Unsupported Type for Field:" + this.name + " desc:" + fn.desc + " in:" + c.name);
			return false;
		}
		
		//Set Cached Values for re-use
		this.cisArr = isArr;
		this.cdt = type;
		
		return true;
	}
	
	public void gc() {}
}