package jredfox.mce.cfg;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

public class CachedInsertionPoint {
	/**
	 * Which direction to insert into BEFORE is backwards and AFTER is forwards
	 */
	private Opperation opp;
	/**
	 * The Originally Described AbstractInsnNode but if the Index is before it's actual insertion point will be insert after the previous instruction
	 */
	private AbstractInsnNode point;
	/**
	 * The Actual Point to Insert to after or null if index is 0
	 */
	public AbstractInsnNode cachedPoint;
	/**
	 * The Insertion Index inside of the InsnList. uses the InsnList Builtin APIs to determine the index and get the AbstractInsnNode later
	 */
	public int index;
	
	public boolean labelBefore;
	public AbstractInsnNode firstInsn;
	public AbstractInsnNode lastInsn;
	
	public CachedInsertionPoint(InsnList l, AbstractInsnNode p, Opperation o, boolean lb)
	{
		this.point = p;
		this.opp = o;
		this.labelBefore = lb;
		int index = o == Opperation.INSERT ? 0 : ((o == Opperation.BEFORE) ? (l.indexOf(p)) : (l.indexOf(p) + 1));
		this.index = index;
		if(index != 0)
			this.cachedPoint = l.get(index - 1);
	}

	@Override
	public String toString()
	{
		return this.opp + " " + this.point + " index:" + this.index + " labelBefore:" + this.labelBefore;
	}
	
	@Override
	public int hashCode()
	{
	    return this.index;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		else if(!(obj instanceof CachedInsertionPoint))
			return false;
		CachedInsertionPoint o = (CachedInsertionPoint) obj;
		return this.index == o.index;
	}

}
