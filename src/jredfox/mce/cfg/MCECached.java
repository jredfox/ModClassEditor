package jredfox.mce.cfg;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
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
	/**
	 * The cached MethodNode that comes from {@link #accept(MethodNode)}
	 */
	public MethodNode method;
	/**
	 * The cached Annotation node that comes from {@link #accept(AnnotationNode)}
	 */
	public AnnotationNode ann;
	
	public MCECached(MCEField field)
	{
		this.mceField = field;
	}
	
	public boolean accept(MethodNode m)
	{
		if(this.accepted && this.mceField.onlyOne)
			return false;
		
		boolean wc = this.mceField.wc;
		boolean wcd = this.mceField.wcd;
		boolean mt = this.mceField.mt;

		if ((wc ? WildCardMatcher.match(m.name, this.mceField.name, true) : m.name.equals(this.mceField.name))
				&& (mt || (wcd ? WildCardMatcher.match(m.desc, this.mceField.desc, true)
						: m.desc.equals(this.mceField.desc)))) 
		{
			this.accepted = true;
			this.method = m;
			return true;
		}
		
		
		return false;
	}
	
	public boolean accept(AnnotationNode ann)
	{
		return false;
	}
	
	/**
	 * Apply All Injections
	 */
	public void apply() 
	{
		if(!this.accepted)
			return;
		
		if(this.method != null)
			this.mceField.apply(this.method, this.point, this.opp);
		else
			this.mceField.apply(this.ann, this.point, this.opp);
		this.clear();
	}

	/**
	 * Clears the Cached Injection Point ONLY doesn't set {@link #accepted} 
	 * to false this is done when on the last MethodNode / AnnotationNode iteration
	 */
	public void clear() 
	{
		this.point = null;
		this.opp = null;
		this.method = null;
		this.ann = null;
	}

}
