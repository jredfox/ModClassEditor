package jredfox.mce.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.ralleytn.simple.json.JSONArray;
import org.ralleytn.simple.json.JSONObject;

import jredfox.mce.ArrCache;
import jredfox.mce.ArrUtils;
import jredfox.mce.types.DataType;
import jredfox.mce.types.InsnTypes;
import jredfox.mce.util.MCECoreUtils;
import jredfox.mce.util.MCEUtil;
import jredfox.mce.util.OpcodeHelper;
import jredfox.mce.util.WildCardMatcher;

/**
 * Allows Classes Fields to be edited as if they were a configuration file
 * @author jredfox
 */
public class MCEObj {
	
	public static final Map<String, MCEObj> registry = new HashMap();
	
	/**
	 * Register an MCEObj
	 * @param class name
	 * @param JSONObject root
	 */
	public static void register(String c, JSONObject root) 
	{
		MCEObj.registry.put(c, new MCEObj(c, root.getJSONObject(c)));
	}
	
	/**
	 * Get the MCEObj required for the Transformer
	 */
	public static MCEObj get(String actualName) 
	{
		return registry.get(actualName);
	}
	
	public String className;
	public String classNameASM;
	public List<MCEField> fields = new ArrayList();
//	public List<MCEFindAndReplace> frs = new ArrayList();
//	public List<MCEParam> params = new ArrayList();
	
	public MCEObj(String className)
	{
		this.className = className.replace('/', '.');
		this.classNameASM = className.replace('.', '/');
	}
	
	public MCEObj(String c, JSONObject json)
	{
		this(c);
		this.parse(json);
	}
	
	public void parse(JSONObject json)
	{
		if(json == null)
			return;
		
		//clear previous fields
		this.fields.clear();
		
		JSONArray arr = json.getJSONArray("Fields");
		for(Object o : arr)
		{
			if(!(o instanceof JSONObject))
				continue;//Why are there comments in here
			
			JSONObject f = (JSONObject) o;
			this.fields.add(!f.containsKey("values") ? new MCEField(f) : new MCEArrField(f));
		}
	}

	public static void configure(String actualName, ClassNode classNode)
	{
		System.out.println("Mod Class Editor:" + actualName);
		MCEObj mce = get(actualName);
		
		//Sanity Check
		if(mce == null) 
		{
			System.err.println("Error Missing " + actualName + " MCEObj from ModClassEditor.JSON this is a BUG!");
			return;
		}
		
		mce.configure(classNode);
	}

	public synchronized void configure(ClassNode cn)
	{
		//Avoid GETFIELD OPCODES
		List<MCEField> cf = this.fields;
		int len = cf.size() - 1;
		
		List<MethodNode> ml = cn.methods;
		int size = ml.size();
		int mi = 0;
		
		for(MethodNode m : ml)
		{
			boolean last = (mi++ + 1) == size;
			List<MCEField> cache = new ArrayList(5);
			for(int i=len; i >= 0; i--) 
			{
				MCEField c = cf.get(i);
				if(c.accept(cn, m))
					cache.add(c);
				else if(last)
					c.clear();
			}
			
			for(MCEField c : cache)
			{
				c.apply();
				if(last)
					c.clear();
			}
		}
	}

	public static boolean equals(InsnTypes type, AbstractInsnNode ab, AbstractInsnNode point) 
	{
		switch(type)
		{
			case LabelNode:
				return ab instanceof LabelNode;
			case FieldInsnNode:
				return ab instanceof FieldInsnNode && MCECoreUtils.equals((FieldInsnNode) ab, (FieldInsnNode) point);
			case MethodInsnNode:
				return ab instanceof MethodInsnNode && MCECoreUtils.equals((MethodInsnNode) ab, (MethodInsnNode) point);
			case InsnNode:
				return ab instanceof InsnNode && MCECoreUtils.equals((InsnNode) ab, (InsnNode) point);
			case IntInsnNode:
				return ab instanceof IntInsnNode && MCECoreUtils.equals((IntInsnNode) ab, (IntInsnNode) point);
			case VarInsnNode:
				return ab instanceof VarInsnNode && MCECoreUtils.equals((VarInsnNode) ab, (VarInsnNode) point);
			case JumpInsnNode:
				return ab instanceof JumpInsnNode && MCECoreUtils.equals((JumpInsnNode) ab, (JumpInsnNode) point);
			case TypeInsnNode:
				return ab instanceof TypeInsnNode && MCECoreUtils.equals((TypeInsnNode) ab, (TypeInsnNode) point);
			case LdcInsnNode:
				return ab instanceof LdcInsnNode && MCECoreUtils.equals((LdcInsnNode) ab, (LdcInsnNode) point);
			case Opcode:
				return MCECoreUtils.equalsOpcode(ab, point);

			default:
				break;
		}
		return false;
	}

}