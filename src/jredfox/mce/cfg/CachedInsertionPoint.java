package jredfox.mce.cfg;

import org.objectweb.asm.tree.AbstractInsnNode;

public class CachedInsertionPoint {
	
	public Opperation opp;
	public AbstractInsnNode point;
	public boolean typeNormal;
	
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
