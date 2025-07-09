package jredfox.mce.cfg;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import jredfox.mce.util.MCEUtil;
import jredfox.mce.util.WildCardMatcher;

/**
 * Cached Data for MCEField as MCEField is READONLY (ThreadSafe to read not to write)
 */
public class MCECached {
	
	/**
	 * Cached MCEField reference
	 */
	public MCEField mceField;
	/**
	 * The real Cached Insertion Point not a Configuration Object like InsertionPoint!
	 */
	public CachedInsertionPoint point;
	/**
	 * weather or not we accepted a MethodNode already
	 */
	public boolean accepted;
	/**
	 * The cached ClassNode that comes from {@link #accept(MethodNode)} or {@link #accept(AnnotationNode)}
	 */
	public ClassNode cn;
	/**
	 * The cached MethodNode that comes from {@link #accept(MethodNode)}
	 */
	public MethodNode method;
	/**
	 * The cached Annotation node that comes from {@link #accept(AnnotationNode)}
	 */
	public AnnotationNode ann;
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
	
	public MCECached(MCEField field)
	{
		this.mceField = field;
		this.wc =  MCEUtil.isWildCard(field.name);
		this.wcd = MCEUtil.isWildCard(field.desc);
		this.mt = field.desc.isEmpty();
		this.onlyOne = !wc && !wcd;
	}
	
	public boolean accept(ClassNode c, MethodNode m)
	{
		if(this.accepted && this.onlyOne)
			return false;
		
		if ((this.wc ? WildCardMatcher.match(m.name, this.mceField.method, true) : m.name.equals(this.mceField.method))
				&& (this.mt || (this.wcd ? WildCardMatcher.match(m.desc, this.mceField.desc, true)
						: m.desc.equals(this.mceField.desc)))) 
		{
			this.point = this.mceField.capture(c, m);
			if(this.point == null)
				return false;
			
			this.accepted = true;
			this.cn = c;
			this.method = m;
			return true;
		}
		
		return false;
	}

	public boolean accept(ClassNode c, AnnotationNode ann)
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
			this.mceField.apply(this.cn, this.method, this.point);
		else
			this.mceField.apply(this.cn, this.ann, this.point);
		this.clear();
	}

	/**
	 * Clears the Cached Injection Point ONLY doesn't set {@link #accepted} 
	 * to false this is done when on the last MethodNode / AnnotationNode iteration
	 */
	public void clear() 
	{
		this.point = null;
		this.method = null;
		this.ann = null;
	}

}
