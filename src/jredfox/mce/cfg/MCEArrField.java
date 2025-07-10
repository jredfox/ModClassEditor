package jredfox.mce.cfg;

import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.ralleytn.simple.json.JSONObject;

import jredfox.mce.types.DataType;
import jredfox.mce.util.MCECoreUtils;
import jredfox.mce.util.MCEUtil;

public class MCEArrField extends MCEField
{
	/**
	 * The list of values going to be applied to the static array
	 */
	public String[] values;
	/**
	 * whether or not the values has a null value used to support wrapper objects and strings
	 */
	public boolean hasNULL;
	/**
	 * used for static arrays as the start index where -1 represents the last index no matter how large or small
	 */
	public int index_start;
	/**
	 * used for static arrays as the end index where -1 repsresents the last index
	 */
	public int index_end;
	/**
	 * used for static arrays as it increments the value by this number each time
	 */
	public int increment;
	/**
	 * when true allows the array to grow WARNING: this creates a new memory location and possible code breaking changes
	 */
	public boolean grow;
	/**
	 * when true inserts values into the index without replacement and grows the array WARNING: this creates a new memory location and possible code breaking changes
	 */
	public boolean append;
	/**
	 * when true replaces the entire array with your values WARNING: this creates a new memory location and possible code breaking changes
	 */
	public boolean replace;
	
	public MCEArrField(){}
	
	public MCEArrField(MCEObj parent, JSONObject json)
	{
		this(parent.classNameASM, json.getString("name"), json.getJSONArray("values"), json.getString("type"), json.getString("method"), json.getString("desc"), new InsertionPoint(json), json.getAsString("index"), json.getAsString("increment"));
	}
		
	public MCEArrField(String owner, String name, List values, String type, String method, String desc, InsertionPoint inject, String index, String increment)
	{
		super(owner, name, null, type, method, desc, inject);
			
		//process values into the String[] array
		if(values != null && !values.isEmpty())
		{
			this.values = new String[values.size()];
			for(int i=0;i<values.size();i++)
			{
				Object o = values.get(i);
				if(o != null)
					this.values[i] = String.valueOf(o);
				else
					this.hasNULL = true;
			}
		}
		else
			this.values = new String[] {""};
		
		//process index
		String[] arr = MCEUtil.splitFirst(MCEUtil.safeString(index, "0").replace("start", "0"), '-');
		String str_start = arr[0];
		String str_end = arr[1];
		this.index_start = str_start.equals("end") ? -1 : MCEUtil.parseInt(str_start);
		this.index_end = str_end.isEmpty() ? this.index_start : (str_end.equals("end") ? -1 : MCEUtil.parseInt(str_end));
			
			//process increment
		this.increment = MCEUtil.parseInt(MCEUtil.safeString(increment, "0"));
	}
	
	@Override
	public void apply(ClassNode cn, MethodNode m, CachedInsertionPoint p)
	{
		if(!this.cisArr)
			return;
		
		if(this.values == null)
			throw new RuntimeException("MCEArrField#values[] has already been garbage collected! Please Disable \"GC Optimizations\" in the Config Or Try Enabling \"BatchLoading\"!");
		
		System.out.println("Applying:" + this.ccn + " " + this.cmn + " " + this.cip);
		FieldNode fn = this.cfn;
		DataType type = this.cdt;
		InsnList list = new InsnList();
		
		list.add(new FieldInsnNode(Opcodes.GETSTATIC, this.owner, this.name, fn.desc));//arr
		if(this.values.length < 2)
		{
			String val = this.values[0];
			if(this.index_start != this.index_end)
			{
				//ArrUtils#fill(arr, v, index_start, index_end, increment); or ArrUtils#fill(arr, v, index_start, index_end);
				list.add(MCECoreUtils.getNumInsn(val, type));//value
				list.add(MCECoreUtils.getIntInsn(this.index_start));//index_start
				list.add(MCECoreUtils.getIntInsn(this.index_end));//index_end
				if(type.hasIncrement)
					list.add(MCECoreUtils.getIntInsn(this.increment));//inecrement
				list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jredfox/mce/ArrUtils", "fill", type.getDescFill(val)));
			}
			else
			{
				//arr_short[index_start] = v;
				//or ArrUtils#set(arr, index, v);
				list.add(MCECoreUtils.getIntInsn(this.index_start));//set the index
				list.add(MCECoreUtils.getNumInsn(val, type));//set the value
				if(this.index_start > -1)
				{
					//convert the primitive datatype into it's object form before using AASTORE
					if(type.isWrapper && val != null)
						list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, type.clazz, "valueOf", type.descValueOf));
					list.add(new InsnNode(type.arrayStore));//stores the value
				}
				else
					list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jredfox/mce/ArrUtils", "set", type.getDescSet(val)));
			}
		}
		else
		{
			//ArrUtils#insert(arr, new arr[]{this.values}, farr.index_start);
			MCECoreUtils.genStaticArraySafe(list, this.values, type, this.hasNULL);
			list.add(MCECoreUtils.getIntInsn(this.index_start));
			list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jredfox/mce/ArrUtils", "insert", type.getDescInsert(this.hasNULL)));
		}
		
		//Inject the code
		this.inject(m, list, p);
	}
		
	@Override
	public void gc() 
	{
		this.values = null;
	}
}