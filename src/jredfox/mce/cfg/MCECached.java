package jredfox.mce.cfg;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import jredfox.mce.util.MCEUtil;
import jredfox.mce.util.WildCardMatcher;

public class MCECached {
	
	/**
	 * Cached MCEField reference
	 */
	public MCEField mceField;
	/**
	 * Cached Real Injection Point
	 */
	public AbstractInsnNode point;
	/**
	 * Cached Real Opperation before or after the point
	 */
	public Opperation opp;
	/**
	 * weather or not we accepted a MethodNode already
	 */
	public boolean accepted;
	
	public MCECached(MCEField field)
	{
		this.mceField = field;
	}
	
	public boolean accept(MethodNode m)
	{
		if(this.accepted && this.mceField.onlyOne)
			return false;
		
		
		return false;
	}
	
	/**
	 * Apply All Injections
	 */
	public void apply() 
	{
		if(!this.accepted)
			return;
	}

	/**
	 * Clears the Cached Injection Point ONLY doesn't set {@link #accepted} 
	 * to false this is done when on the last MethodNode / AnnotationNode iteration
	 */
	public void clear() 
	{
		this.point = null;
		this.opp = null;
	}

}
