package jredfox.mce.cfg;

import org.objectweb.asm.tree.AbstractInsnNode;

public class CachedInsertionPoint {
	
	public Opperation opp;
	public AbstractInsnNode point;
	public boolean typeNormal;
	public boolean labelBefore;
	public AbstractInsnNode firstInsn;
	public AbstractInsnNode lastInsn;
	
	public CachedInsertionPoint(AbstractInsnNode p, Opperation o, boolean n, boolean lb)
	{
		this.point = p;
		this.opp = o;
		this.typeNormal = n;
		this.labelBefore = lb;
	}
	
	@Override
	public String toString()
	{
		return this.opp + " " + this.point + " typeNormal:" + this.typeNormal;
	}

}
