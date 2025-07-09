package jredfox.mce.cfg;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.MethodNode;
import org.ralleytn.simple.json.JSONObject;

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
		
		//cache booleans
		this.wc = MCEUtil.isWildCard(this.name);
		this.wcd = MCEUtil.isWildCard(this.desc);
		this.mt = this.name.isEmpty();
		this.onlyOne = !MCEUtil.isWildCard(this.name) && !MCEUtil.isWildCard(this.desc);
	}

	public void gc() {}

	public void apply(MethodNode m, AbstractInsnNode point, Opperation opp)
	{
		
	}

	public void apply(AnnotationNode ann, AbstractInsnNode point, Opperation opp)
	{
		
	}
}