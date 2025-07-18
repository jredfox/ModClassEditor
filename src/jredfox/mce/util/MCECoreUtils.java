package jredfox.mce.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InnerClassNode;
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
import org.ralleytn.simple.json.internal.Util;

import jredfox.mce.ArrCache;
import jredfox.mce.ArrUtils;
import jredfox.mce.Transformer;
import jredfox.mce.types.DataType;
import jredfox.mce.types.InsnTypes;

public class MCECoreUtils {
	
	public static int ASM_VERSION = detectASMVersion();

	private static int detectASMVersion() 
	{
		for(int i=5;i<=9;i++)
		{
			try 
			{
				if (Opcodes.class.getField("ASM" + i) != null)
					return i;
			} catch (Throwable ignored) {}
		}
		return 4;
	}
	
	public static ClassNode getClassNode(byte[] basicClass)
	{
		ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        return classNode;
	}

	public static ClassWriter getClassWriter(ClassNode classNode, int flags) 
	{
        ClassWriter classWriter = new ClassWriter(flags);
        classNode.accept(classWriter);
        return classWriter;
	}
	
	public static byte[] toByteArray(ClassWriter classWriter, String transformedName, byte[] org) throws IOException 
	{
        byte[] bytes = classWriter.toByteArray();
        if(Transformer.dump)
        	dumpFile(transformedName, bytes);
    	if(Transformer.dumpOrg && org != null)
    		dumpFile(transformedName + "_org", org);
        
        return bytes;
	}
	
	public static void pubMinusFinal(ClassNode classNode, boolean all)
	{
		//AT the fields
		for(FieldNode f : classNode.fields)
		    f.access = pubMinusFinal(f.access);
		
		if(all)
		{
			//AT the methods
			for(MethodNode m : classNode.methods)
				m.access = pubMinusFinal(m.access);
			
			//AT the class
			classNode.access = pubMinusFinal(classNode.access);
			
			//AT the inner classes if non null
			if(classNode.innerClasses != null)
				for(InnerClassNode inner : classNode.innerClasses)
					inner.access = pubMinusFinal(inner.access);
		}
	}
	
	public static int pubMinusFinal(int access) 
	{
	    // Remove conflicting access modifiers
	    access &= ~(Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED);
	    
	    // Remove the final modifier
	    access &= ~Opcodes.ACC_FINAL;
	    
	    // Set the public modifier
	    access |= Opcodes.ACC_PUBLIC;
	    
	    return access;
	}
	
	public static MethodNode getMethodNode(ClassNode classNode, String method_name, String method_desc) 
	{
		for (Object method_ : classNode.methods)
		{
			MethodNode method = (MethodNode) method_;
			if (method.name.equals(method_name) && method.desc.equals(method_desc))
			{
				return method;
			}
		}
		return null;
	}
	
	/**
	 * Gets the Last LabelNode either before the return of the method or last label
	 */
	public static AbstractInsnNode getLastLabelNode(MethodNode method, boolean afterReturn)
	{
		AbstractInsnNode[] arr = method.instructions.toArray();
		boolean found = afterReturn;
		for(int i=arr.length-1;i>=0;i--)
		{
			AbstractInsnNode ab = arr[i];
			if(ab == null)
				continue;
			if(!found && isReturnOpcode(ab.getOpcode()))
				found = true;
			
			if(found && (ab instanceof LabelNode || ab instanceof LineNumberNode))
			{
				return ab;
			}
		}
		return null;
	}
	
	/**
	 * Gets the Last LabelNode either before the return of the method or last label
	 */
	public static AbstractInsnNode getLastReturn(MethodNode method)
	{
		AbstractInsnNode[] arr = method.instructions.toArray();
		for(int i=arr.length-1;i>=0;i--)
		{
			AbstractInsnNode ab = arr[i];
			if(ab == null)
				continue;
			if(isReturnOpcode(ab.getOpcode()))
				return ab;
		}
		return null;
	}
	
	public static boolean isReturnOpcode(int opcode)
	{
		return opcode == Opcodes.RETURN || opcode == Opcodes.ARETURN || opcode == Opcodes.DRETURN || opcode == Opcodes.FRETURN || opcode == Opcodes.IRETURN || opcode == Opcodes.LRETURN;
	}
	
	/**
	 * dumps a file from memory
	 * @throws IOException 
	 */
	public static void dumpFile(String name, byte[] bytes)
	{
    	name = name.replace('.', '/');
    	File f = new File(System.getProperty("user.dir"), "asm/dumps/mce/" + name + ".class").getAbsoluteFile();
    	f.getParentFile().mkdirs();
    	InputStream in = null;
    	OutputStream out = null;
    	try
    	{
    		in = new ByteArrayInputStream(bytes);
    		out = new FileOutputStream(f);
    		copy(in, out);
    	}
    	catch(Throwable e)
    	{
    		e.printStackTrace();
    	}
    	finally
    	{
    		Util.close(in, out);
    	}
	}
	
	public static void copy(InputStream in, OutputStream out) throws IOException
	{
		byte[] buffer = new byte[1048576/2];
		int length;
   	 	while ((length = in.read(buffer)) >= 0)
		{
			out.write(buffer, 0, length);
		}
	}

	public static FieldNode getFieldNode(String name, ClassNode classNode) {
		for (FieldNode fn : classNode.fields) {
			if (fn.name.equals(name)) {
				return fn;
			}
		}
		return null;
	}
	
	public static AbstractInsnNode prevSkipFrames(AbstractInsnNode a)
	{
		if(a == null)
			return null;
		
		do
		{
			a = a.getPrevious();
		} while (a != null && a instanceof FrameNode);
		
		return a;
	}
	
	public static AbstractInsnNode nextSkipFrames(AbstractInsnNode a)
	{
		if(a == null)
			return null;
		
		do
		{
			a = a.getNext();
		} while (a != null && a instanceof FrameNode);
		
		return a;
	}
	
	/**
	 * Iterates Until Next / Previous is not a FrameNode. Will not iterate if it's not currently a FrameNode
	 */
	public static AbstractInsnNode skipFrames(AbstractInsnNode a, boolean nxt)
	{
		while(a != null && a instanceof FrameNode)
			a = nxt ? a.getNext() : a.getPrevious();
		return a;
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
				return ab != null && MCECoreUtils.equalsOpcode(ab, point);
			case IincInsnNode:
				return ab instanceof IincInsnNode && MCECoreUtils.equals((IincInsnNode) ab, (IincInsnNode)point);
			case NULL:
				return ab == null && point == null;

			default:
				break;
		}
		return false;
	}

	public static boolean equals(InsnNode a, InsnNode b)
	{
		return a.getOpcode() == b.getOpcode();
	}
	
	public static boolean equals(JumpInsnNode a, JumpInsnNode b)
	{
		return a.getOpcode() == b.getOpcode();
	}
	
	public static boolean equals(LineNumberNode a, LineNumberNode b)
	{
		return a.line == b.line;
	}
	
	public static boolean equals(IntInsnNode a, IntInsnNode b)
	{
		return a.getOpcode() == b.getOpcode() && a.operand == b.operand;
	}
	
	public static boolean equals(LdcInsnNode a, LdcInsnNode b)
	{
		return a.cst != null ? (a.cst.equals(b.cst)) : (b.cst == null);
	}
	
	public static boolean equals(MethodInsnNode obj1, MethodInsnNode obj2)
	{
		return obj1.getOpcode() == obj2.getOpcode() && obj1.name.equals(obj2.name) && obj1.desc.equals(obj2.desc) && obj1.owner.equals(obj2.owner);
	}
	
	public static boolean equals(TypeInsnNode compare, TypeInsnNode ab) 
	{
		return compare.getOpcode() == ab.getOpcode() && compare.desc.equals(ab.desc);
	}
	
	public static boolean equals(VarInsnNode obj1, VarInsnNode obj2)
	{
		return obj1.getOpcode() == obj2.getOpcode() && obj1.var == obj2.var;
	}
	
	public static boolean equals(FieldInsnNode a, FieldInsnNode b)
	{
		return a.getOpcode() == b.getOpcode() && a.name.equals(b.name) && a.owner.equals(b.owner) && a.desc.equals(b.desc);
	}
	
	public static boolean equalsOpcode(AbstractInsnNode a, AbstractInsnNode b)
	{
		return a.getOpcode() == b.getOpcode();
	}
	
	public static boolean equals(IincInsnNode a, IincInsnNode b)
	{
		return a.var == b.var && a.incr == b.incr;
	}
	
	public static void addLabelNode(InsnList list)
	{
		if(!Transformer.label)
			return;
		
		LabelNode l1 = new LabelNode();
		list.add(l1);
		if(ASM_VERSION < 5)
			list.add(new LineNumberNode(0, l1));//Force Labels to be created so JIT can do it's Job and optimize code
	}
	
	public static void insertLabelNode(InsnList list)
	{
		if(!Transformer.label)
			return;
		
		LabelNode l1 = new LabelNode();
		if(ASM_VERSION < 5)
			list.insert(new LineNumberNode(0, l1));//Force Labels to be created so JIT can do it's Job and optimize code
		list.insert(l1);
	}
	
	public static void insertLabelNode(InsnList list, AbstractInsnNode spot)
	{
		if(!Transformer.label)
			return;
		
		InsnList l = new InsnList();
		LabelNode label = new LabelNode();
		l.add(label);
		if(ASM_VERSION < 5)
			l.add(new LineNumberNode(0, label));//Force Labels to be created so JIT can do it's Job and optimize code
		list.insert(spot, l);
	}
	
	/**
	 * Unused Now But allows you to search for a list of MethodNode with wildcard support and EMPTY DESC Will match ANY first DESC
	 */
	public static List<MethodNode> getMethodNodes(ClassNode classNode, String method_name, String method_desc) {
		boolean wc = method_name.contains("*") || method_name.contains("?");
		boolean wcd = method_desc.contains("*") || method_desc.contains("?");
		boolean mt = method_desc.trim().isEmpty();

		List<MethodNode> l = new ArrayList<MethodNode>(wc ? 10 : 3);
		for (Object method_ : classNode.methods) {
			MethodNode method = (MethodNode) method_;

			if ((wc ? WildCardMatcher.match(method.name, method_name, true) : method.name.equals(method_name))
					&& (mt || (wcd ? WildCardMatcher.match(method.desc, method_desc, true)
							: method.desc.equals(method_desc)))) {
				l.add(method);
				if (!wc && !wcd)
					break;
			}
		}
		return l;
	}
	
	public static AbstractInsnNode getFirstCtrInsn(ClassNode cn, MethodNode m) 
	{
		AbstractInsnNode a = m.instructions.getFirst();
		while(a != null)
		{
			if(a.getOpcode() == Opcodes.INVOKESPECIAL && a instanceof MethodInsnNode)
			{
				MethodInsnNode am = (MethodInsnNode) a;
				if(am.name.equals("<init>") && (am.owner.equals(cn.name) || am.owner.equals(cn.superName)))
				{
					AbstractInsnNode nxt = nextRealInsn(am);
					
					//if the return instruction appears right after init in rare cases assume this is the last injection point
					if(nxt == null || MCECoreUtils.isReturnOpcode(nxt.getOpcode()))
						return am;
					
					AbstractInsnNode prev = prevRealInsn(am);
					if(prev != null && !OpcodeHelper.BAD_CTR_OPCODES.contains(prev.getOpcode()))
						return am;
				}
			}
			a = a.getNext();//increment the index
		}
		return null;
	}

	public static AbstractInsnNode nextRealInsn(AbstractInsnNode a) 
	{
		do
		{
			a = a.getNext();
		}
		while (a != null && a.getOpcode() == -1);
		
		return a;
	}
	
	public static AbstractInsnNode prevRealInsn(AbstractInsnNode a) 
	{
		do
		{
			a = a.getPrevious();
		}
		while (a != null && a.getOpcode() == -1);
		
		return a;
	}

	/**
	 * Generates a Static Array Safely and if bigger then size of 10 will generate it now and cache it to prevent exceeding the bytecode limit
	 */
	public static void genStaticArraySafe(InsnList list, String[] values, DataType type, boolean wrappers)
	{
		if(values.length < 11)
		{
			genStaticArray(list, values, type, wrappers);
			return;
		}
		if(!wrappers)
			type = DataType.getPrimitive(type);
		int id = ArrCache.register(ArrUtils.newArr(values, type));
		list.add(getIntInsn(id));
		list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jredfox/mce/ArrCache", "get", "(I)Ljava/lang/Object;"));
		list.add(new TypeInsnNode(Opcodes.CHECKCAST, "[" + type.desc));
	}

	/**
	 * Generates a static array initialized with specified values
	 * @param list the list to generate the bytecode into list#add will be called so keep that in mind no injections points will be present
	 * @param values a string list of values representing any data type and will get parsed based on the type param
	 * @param type defines what data type of static array to create
	 * @WARNING: DO NOT USE past 10 indexes as Java's max bytecode limit per method is 65535 bytes. This method works but is depreciated please use the ArrUtils#gen instead
	 */
	public static void genStaticArray(InsnList list, String[] values, DataType type, boolean wrappers) 
	{
		if(!wrappers)
			type = DataType.getPrimitive(type);
		
		//array size
		list.add(getIntInsn(values.length));
		
		//array type
		if(!type.isObject)
			list.add(new IntInsnNode(Opcodes.NEWARRAY, type.newArrayType));
		else
			list.add(new TypeInsnNode(Opcodes.ANEWARRAY, type.clazz));
		
		//initialize NON-ZERO VALUES
		boolean isWrapper = type.isWrapper;
		for(int index=0; index < values.length; index++)
		{
			String str_v = values[index];
			if(str_v == null)
				continue;//SKIP NULL or 0 Values as it's already null / zero from creating the new array
			AbstractInsnNode valInsn = null;
			switch(type)
			{
				case WRAPPED_BOOLEAN:
				case BOOLEAN:
				{
					boolean v = MCEUtil.parseBoolean(str_v);
					if(!v && !isWrapper)
						continue;
					valInsn = getBoolInsn(v);
				}
				break;
				case WRAPPED_BYTE:
				case BYTE:
				{
					byte v = MCEUtil.parseByte(str_v);
					if(v == 0 && !isWrapper)
						continue;
					valInsn = getIntInsn(v);
				}
				break;
				case WRAPPED_SHORT:
				case SHORT:
				{
					short v = MCEUtil.parseShort(str_v);
					if(v == 0 && !isWrapper)
						continue;
					valInsn = getIntInsn(v);
				}
				break;
				case WRAPPED_INT:
				case INT:
				{
					int v = MCEUtil.parseInt(str_v);
					if(v == 0 && !isWrapper)
						continue;
					valInsn = getIntInsn(v);
				}
				break;
				case WRAPPED_LONG:
				case LONG:
				{
					long v = MCEUtil.parseLong(str_v);
					if(v == 0 && !isWrapper)
						continue;
					valInsn = getLongInsn(v);
				}
				break;
				case WRAPPED_FLOAT:
				case FLOAT:
				{
					float v = MCEUtil.parseFloat(str_v);
					if(v == 0 && !isWrapper)
						continue;
					valInsn = getFloatInsn(v);
				}
				break;
				case WRAPPED_DOUBLE:
				case DOUBLE:
				{
					double v = MCEUtil.parseDouble(str_v);
					if(v == 0 && !isWrapper)
						continue;
					valInsn = getDoubleInsn(v);
				}
				break;
				case STRING:
					valInsn = new LdcInsnNode(str_v);
				break;
				
				case WRAPPED_CHAR:
				case CHAR:
				{
					char v = MCEUtil.parseChar(str_v);
					if(v == (char)0 && !isWrapper)
						continue;
					valInsn = getIntInsn(v);
				}
				break;
				
				default:
					break;
			}
			
			list.add(new InsnNode(Opcodes.DUP));
			list.add(getIntInsn(index));//indexes are always integer and follow the same rules as any boolean - int values on pushing
			list.add(valInsn);//changes based on the data type
			if(isWrapper)
				list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, type.clazz, "valueOf", type.descValueOf));//converts the datatype into it's Object form
			list.add(new InsnNode(type.arrayStore));
		}
	}
	
	public static AbstractInsnNode getNumInsn(String str_v, DataType type)
	{
		//NULL support
		if(str_v == null)
			return getNullInsn(type);
		
		switch(type)
		{
			case WRAPPED_BOOLEAN:
			case BOOLEAN:
				return getBoolInsn(MCEUtil.parseBoolean(str_v));
			case WRAPPED_BYTE:
			case BYTE:
				return getIntInsn(MCEUtil.parseByte(str_v));
			case WRAPPED_SHORT:
			case SHORT:
				return getIntInsn(MCEUtil.parseShort(str_v));
			case WRAPPED_INT:
			case INT:
				return getIntInsn(MCEUtil.parseInt(str_v));
			case WRAPPED_LONG:
			case LONG:
				return getLongInsn(MCEUtil.parseLong(str_v));
			case WRAPPED_FLOAT:
			case FLOAT:
				return getFloatInsn(MCEUtil.parseFloat(str_v));
			case WRAPPED_DOUBLE:
			case DOUBLE:
				return getDoubleInsn(MCEUtil.parseDouble(str_v));
			case STRING:
				return new LdcInsnNode(str_v);
			case WRAPPED_CHAR:
			case CHAR:
				return getIntInsn(MCEUtil.parseChar(str_v));
			default:
				break;
		}
		return null;
	}

	public static AbstractInsnNode getNullInsn(DataType type) 
	{
		if(type.isObject)
			return new InsnNode(Opcodes.ACONST_NULL);
		
		//Handle NULL Primitives
		switch(type)
		{
			case CHAR:
			case BOOLEAN:
			case BYTE:
			case SHORT:
			case INT:
				return getIntInsn(0);
			case LONG:
				return getLongInsn(0);
			case FLOAT:
				return getFloatInsn(0);
			case DOUBLE:
				return getDoubleInsn(0);

			default:
				return null;
		}
	}

	public static InsnNode getBoolInsn(boolean v) 
	{
		return new InsnNode(v ? Opcodes.ICONST_1 : Opcodes.ICONST_0);
	}

	public static AbstractInsnNode getIntInsn(int v) 
	{
		InsnNode insn = getConstantInsn(v);
		if(insn != null)
			return insn;
		else if (v >= Byte.MIN_VALUE && v <= Byte.MAX_VALUE)
			return new IntInsnNode(Opcodes.BIPUSH, v);
		else if (v >= Short.MIN_VALUE && v <= Short.MAX_VALUE)
			return new IntInsnNode(Opcodes.SIPUSH, v);
		return new LdcInsnNode(new Integer(v));
	}
	
	public static AbstractInsnNode getLongInsn(long v) 
	{
		AbstractInsnNode cst = getConstantInsn(v);
		return cst == null ? new LdcInsnNode(new Long(v)) : cst;
	}
	
	public static AbstractInsnNode getFloatInsn(float v)
	{
		AbstractInsnNode cst = getConstantInsn(v);
		return cst == null ? new LdcInsnNode(new Float(v)) : cst;
	}
	
	public static AbstractInsnNode getDoubleInsn(double v)
	{
		AbstractInsnNode cst = getConstantInsn(v);
		return cst == null ? new LdcInsnNode(new Double(v)) : cst;
	}

	/**
	 * longs, double and float behave completely different in the bytecode compared to boolean, byte, short and int
	 */
	private static InsnNode getConstantInsn(long v) 
	{
		return v == 0 ? new InsnNode(Opcodes.LCONST_0) : (v == 1 ? new InsnNode(Opcodes.LCONST_1) : null);
	}
	
	private static InsnNode getConstantInsn(float v) 
	{
		return v == 0 ? new InsnNode(Opcodes.FCONST_0) : (v == 1 ? new InsnNode(Opcodes.FCONST_1) : (v == 2 ? new InsnNode(Opcodes.FCONST_2) : null) );
	}
	
	private static InsnNode getConstantInsn(double v) 
	{
		return v == 0 ? new InsnNode(Opcodes.DCONST_0) : (v == 1 ? new InsnNode(Opcodes.DCONST_1) : null);
	}

	private static InsnNode getConstantInsn(int b) 
	{
		if (b > -2 && b < 6)
		{
			int opcode = 0;
			switch(b)
			{
				case -1:
					opcode = Opcodes.ICONST_M1;
				break;
				
				case 0:
					opcode = Opcodes.ICONST_0;
				break;
				
				case 1:
					opcode = Opcodes.ICONST_1;
				break;
				
				case 2:
					opcode = Opcodes.ICONST_2;
				break;
				
				case 3:
					opcode = Opcodes.ICONST_3;
				break;
				
				case 4:
					opcode = Opcodes.ICONST_4;
				break;
				
				case 5:
					opcode = Opcodes.ICONST_5;
				break;
			}
			return new InsnNode(opcode);
		}
		
		return null;
	}
	
	public static final String UNSUPPORTED = "Unsupported";

	public static String getType(String desc) {
		//Static array support
		if(desc.startsWith("[")){
			String type = getType(desc.replace("[", ""));
			return UNSUPPORTED.equals(type) ? UNSUPPORTED : (desc.substring(0, desc.lastIndexOf('[') + 1) + type);
		}
		
		return desc.equals("B") ? "byte"
				: desc.equals("S") ? "short"
						: desc.equals("I") ? "int"
								: desc.equals("J") ? "long"
										: desc.equals("Ljava/lang/String;") ? "string"
												: desc.equals("Z") ? "boolean"
														: desc.equals("F") ? "float"
																: desc.equals("D") ? "double"
																			: desc.equals("C") ? "char"
																		: desc.equals("Ljava/lang/Boolean;") ? "Boolean"
																: desc.equals("Ljava/lang/Byte;") ? "Byte"
															: desc.equals("Ljava/lang/Short;") ? "Short"
														: desc.equals("Ljava/lang/Integer;") ? "Integer"
												: desc.equals("Ljava/lang/Long;") ? "Long"
										: desc.equals("Ljava/lang/Float;") ? "Float"
								: desc.equals("Ljava/lang/Double;") ? "Double"
							: desc.equals("Ljava/lang/Character;") ? "Character"
						: UNSUPPORTED;
	}

	public static boolean isLineOrLabel(AbstractInsnNode p) 
	{
		return p instanceof LineNumberNode || p instanceof LabelNode;
	}

	/**
	 * Hash Code for all InsnTypes Supported
	 */
	public static int hash(InsnTypes type, AbstractInsnNode point) 
	{
		if(point == null)
			return 0;
		
		switch(type)
		{
			case InsnNode:
			case Opcode:
			case JumpInsnNode:
				return point.getOpcode();
			case IincInsnNode:
			{
				IincInsnNode v = (IincInsnNode)point;
				return 31 * v.var + v.incr;
			}
			case VarInsnNode:
			{
				VarInsnNode v = (VarInsnNode)point;
				return 31 * v.getOpcode() + v.var;
			}
			case IntInsnNode:
			{
				IntInsnNode v = (IntInsnNode)point;
				return 31 * v.getOpcode() + v.operand;
			}
			case TypeInsnNode:
				return ((TypeInsnNode)point).desc.hashCode();
			case LdcInsnNode:
				return ((LdcInsnNode)point).cst.hashCode();
			case FieldInsnNode:
			{
				FieldInsnNode v = (FieldInsnNode) point;
				return 31 * v.getOpcode() + (v.name + v.desc + v.owner).hashCode();
			}
			case MethodInsnNode:
			{
				MethodInsnNode v = (MethodInsnNode) point;
				return 31 * v.getOpcode() + (v.name + v.desc + v.owner).hashCode();
			}
			case LabelNode:
				return 31 * Opcodes.GOTO;
			case LineNumberNode:
				return ((LineNumberNode)point).line;

			default:
				break;
		}
		return 0;
	}

	public static LineNumberNode nextLineNumberNode(AbstractInsnNode a) 
	{
		AbstractInsnNode index = a.getNext();
		while(index != null)
		{
			if(index instanceof LineNumberNode)
				return (LineNumberNode) index;
			index = index.getNext();
		}
		return null;
	}

}
