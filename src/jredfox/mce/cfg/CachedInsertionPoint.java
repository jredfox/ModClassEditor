package jredfox.mce.cfg;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

public class CachedInsertionPoint {
	
	public Opperation opp;
	public AbstractInsnNode point;
	public boolean typeNormal;
	
	public boolean beforeLabel;
	public boolean afterLabel;
	/**
	 * When Injecting code used as storage of the InsnList firt instruction for LabelNode insertion
	 */
	public AbstractInsnNode firstInsn;
	/**
	 * When Injecting code used as storage of the InsnList last instruction for LabelNode insertion
	 */
	public AbstractInsnNode lastInsn;

	
	public CachedInsertionPoint(AbstractInsnNode p, Opperation o, boolean n)
	{
		this.point = p;
		this.opp = o;
		this.typeNormal = n;
	}
	
	@Override
	public String toString()
	{
		return this.opp + " " + this.point + " typeNormal:" + this.typeNormal;
	}

}
