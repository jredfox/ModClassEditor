package jredfox.mce.cfg;

import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.ralleytn.simple.json.JSONObject;

import jredfox.mce.Transformer;
import jredfox.mce.types.DataType;
import jredfox.mce.types.DataTypeContainer;
import jredfox.mce.types.InsnTypes;
import jredfox.mce.util.MCECoreUtils;
import jredfox.mce.util.MCEUtil;
import jredfox.mce.util.WildCardMatcher;

public class MCEField
{
	/**
	 * The Transformed & Deobfuscated Class Name that is the Class we are Transforming
	 */
	public String clazzName;
	/**
	 * The Transformed & Deobfuscated Class Name that is the Owner of the Field
	 */
	public String owner;
	/**
	 * the type [String, char, boolean, byte, short, int, long, float, double, Boolean, Character, Byte, Short, Integer, Long, Float, Double]
	 */
	public DataTypeContainer typec;
	/**
	 * The name of the Field
	 */
	public String name;
	/**
	 * the value to replace
	 */
	public String value;
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
	 * when true we have a custom class that's not matching the current class
	 */
	public boolean custom;
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
	/**
	 * The Reference to the MCEObj's Dynamic Setter MethodNode Cache when {@link MCEObj#configure(ClassNode)} is running
	 */
	public Map<CachedInsertionPoint, MethodNode> dsMap;
	/**
	 * The cached Insertion Point of the Invokation Hook of the Dynamic Setter
	 */
	public CachedInsertionPoint ocip;
	/**
	 * The cached MethodNode that comes from {@link #accept(MethodNode)}
	 */
	public MethodNode ocmn;

	
	public MCEField()
	{
		
	}
		
	public MCEField(MCEObj parent, JSONObject json)
	{
		this(parent.classNameASM, json.findStringDef("", '.', '/', "class", "owner"), json.getString("type"), json.getString("name"), json.getAsStringN("value"), json.getString("method"), json.getString("desc"), new InsertionPoint(json));
	}
	
	public MCEField(String clazzNameASM, String owner, String type, String name, String value, String method, String desc, InsertionPoint inject)
	{
		this.clazzName = clazzNameASM;
		if(owner.isEmpty())
		{
			this.owner = clazzNameASM;
		}
		else
		{
			this.owner = owner;
			this.custom = true;
		}
		this.typec = new DataTypeContainer(MCEUtil.safeString(type).trim(), false);
		this.name = name.trim();
		this.value = value;
		this.method = MCEUtil.safeString(method, "<clinit>").trim();
		this.desc = MCEUtil.safeString(desc).trim();
		this.inject = inject;
		//cache frequently used booleans
		this.wc =  MCEUtil.isWildCard(this.method);
		this.wcd = MCEUtil.isWildCard(this.desc);
		this.mt = this.desc.isEmpty();
		this.onlyOne = !this.wc && !this.wcd;
		
		System.out.println("DEBUG:" + this.owner + " " + this.typec.type + " " + this.name + " " + this.method + " " + this.inject);
	}
	
	public boolean accept(ClassNode cn, Map<CachedInsertionPoint, MethodNode> dsc, MethodNode m)
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
			
			this.ocip = null;
			this.ccn = cn;
			this.cmn = m;
			this.dsMap = dsc;
			this.accepted = true;
			return true;
		}
		
		return false;
	}
	
	private static final String base = "mce_setter_";
	public void genDynamicSetter()
	{
		Map<CachedInsertionPoint, MethodNode> dsm = this.dsMap;
		boolean nr = this.ocip == null;
		MethodNode cachedMN = dsm.get(nr ? (this.cip) : (this.ocip));
		if(cachedMN != null)
		{
			if(nr)
			{
				this.ocmn = this.cmn;
				this.ocip = this.cip;
				this.cmn = cachedMN;
				this.cip = new CachedInsertionPoint(null, null, Opperation.INSERT, true);
			}
			return;
		}
		
		int size = dsm.size();
		ClassNode c = this.ccn;
		while(MCECoreUtils.getMethodNode(c, (base + size), "()V") != null)
			size++;
		String setName = base + size;
		MethodNode m = new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, setName, "()V", null, null);
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
		
		//Add the method to the cache
		dsm.put(this.cip, m);
		
		//Hook Dynamic Setter
		InsnList list = new InsnList();
		list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, this.clazzName, setName, "()V"));
		this.inject(this.cmn, list, this.cip);
		
		//Cache MethodNode / Old CachedInjectionPoint
		this.ocmn = this.cmn;
		this.ocip = this.cip;
		//Re-Direct MethodNode
		this.cmn = m;
		//Re-Direct the CachedInsertionPoint
		this.cip = new CachedInsertionPoint(null, null, Opperation.INSERT, true);
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
		this.cdtc = null;
		this.dsMap = null;
		this.ocmn = null;
		this.ocip = null;
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
		if(this.cdtc.isArr)
			return;
		
		System.out.println("Applying:" + cn.name + " " + m.name + " " + m.desc + " " + p);
		DataType type = this.cdtc.type;
		String desc = this.cdtc.desc;
		
		InsnList list = new InsnList();
		list.add(MCECoreUtils.getNumInsn(this.value, type));
		if(type.isWrapper && this.value != null)
			list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, type.clazz, "valueOf", type.descValueOf));
		list.add(new FieldInsnNode(Opcodes.PUTSTATIC, this.owner, this.name, desc));
		
		//Inject the code
		this.inject(m, list, p);
	}
	
	public void apply(ClassNode cn, AnnotationNode ann, CachedInsertionPoint cip)
	{
		
	}
	
	public void applyLabel()
	{
		this.applyLabel(this.ocmn, this.ocip);
		this.applyLabel(this.cmn,  this.cip);
	}

	public void applyLabel(MethodNode m, CachedInsertionPoint cip)
	{
		if(cip == null || cip.firstInsn == null)
			return;
		
		InsnList l = new InsnList();
		LabelNode l0 = new LabelNode();
		l.add(l0);
		if(MCECoreUtils.ASM_VERSION < 5)
			l.add(new LineNumberNode(0, l0));
		
		AbstractInsnNode prev = MCECoreUtils.prevSkipFrames(cip.firstInsn);
		AbstractInsnNode next = MCECoreUtils.nextSkipFrames(cip.lastInsn);
		if(cip.labelBefore)
			m.instructions.insertBefore(cip.firstInsn, l);
		else
			m.instructions.insert(cip.lastInsn, l);
	}

	public void inject(MethodNode m, InsnList list, CachedInsertionPoint cip) 
	{
		if(Transformer.label)
		{
			cip.firstInsn = list.getFirst();
			cip.lastInsn =  list.getLast();
		}
		
		if(cip.index == 0)
			m.instructions.insert(list);
		else
			m.instructions.insert(cip.cachedPoint, list);
	}

	/**
	 * Captures the Real Injection Point with the actual Opperation & type
	 */
	public CachedInsertionPoint capture(ClassNode cn, MethodNode m) 
	{
		InsertionPoint in = this.inject;
		Opperation opp = in.opp;
		if(in.type == InsnTypes.NULL)
		{
			if(opp == Opperation.AFTER)
			{
				return new CachedInsertionPoint(m.instructions, MCECoreUtils.getLastReturn(m).getPrevious(), Opperation.AFTER, false);
			}
			else if(opp == Opperation.BEFORE)
			{
				if(m.name.equals("<init>"))
					return new CachedInsertionPoint(m.instructions, MCECoreUtils.getFirstCtrInsn(cn, m), Opperation.AFTER, true);
				else
					return new CachedInsertionPoint(null, null, Opperation.INSERT, true);
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
		boolean aftr = opp == Opperation.AFTER;
		
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
							if(type == InsnTypes.LabelNode && opp == Opperation.AFTER)
							{
								AbstractInsnNode nxt = MCECoreUtils.nextSkipFrames(spot);
								if(nxt instanceof LineNumberNode)
									spot = nxt;
							}
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
			insnIndex = aftr ? (insnIndex.getNext()) : (insnIndex.getPrevious());
		}
		while(insnIndex != null && !hasFoundShift);
		
		boolean exact = shiftTo == ShiftTo.EXACT;
		//If inject == spot then We never shifted so we want to insertBefore if the opperation was before
		//If shiftTo is Exact use Exact indexes and always inject before. Index 0 = insertBefore exact insn, Index 1 = insertBefore of the previous instruction
		if(opp == Opperation.BEFORE && (inject == spot || exact))
		{
			AbstractInsnNode prv = MCECoreUtils.prevSkipFrames(spot);
			return new CachedInsertionPoint(m.instructions, spot, Opperation.BEFORE, (prv == null || !MCECoreUtils.isLineOrLabel(prv)));
		}
		else
		{
			AbstractInsnNode nt = MCECoreUtils.skipFrames(spot, false);
			return new CachedInsertionPoint(m.instructions, spot, Opperation.AFTER, (nt == null || !MCECoreUtils.isLineOrLabel(nt)));
		}
	}
	
	protected DataTypeContainer cdtc;
	public boolean canEdit(ClassNode c)
	{
		this.chkErr = true;
		
		//Support editing another classes fields
		if(this.custom)
		{
			this.cdtc = this.typec.copy();
			return this.isNonNullType(this.cdtc, true);
		}
		
		FieldNode fn = MCECoreUtils.getFieldNode(this.name, c);
		
		//Sanity Checks
		if(fn == null)
		{
			System.err.println("Field not Found:" + this.name + " in: " + this.clazzName);
			return false;
		}
		else if((fn.access & Opcodes.ACC_STATIC) == 0)
		{
			System.err.println("Object Fields are not Supported for Editing yet as it's more complex and Per Object to Edit:" + this.name + " in:" + this.clazzName);
			return false;
		}
		
		this.cdtc = new DataTypeContainer(fn.desc, true);
		return this.isNonNullType(this.cdtc, false);
	}
	
	protected boolean isNonNullType(DataTypeContainer c, boolean custom)
	{
		if(c.isNULL)
		{
			if(custom)
				System.err.println("Field is Type is Missing or Unsupported! owner:\"" + this.owner + "\" name:" + this.name + " in:" + this.clazzName);
			else
				System.err.println("Unsupported Type for Field:" + this.name + " desc:" + this.cdtc.desc + " in:" + this.clazzName);
			return false;
		}
		return true;
	}

	public void gc() {}
}