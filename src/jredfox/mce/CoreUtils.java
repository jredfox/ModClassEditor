package jredfox.mce;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InnerClassNode;
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

public class CoreUtils {
	
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
        if(Boolean.parseBoolean(System.getProperty("asm.dump", "false")))
        {
        	dumpFile(transformedName, bytes);
//        	if(org != null)
//        		dumpFile(transformedName + "_org", org);
//TODO: UNCOMMENT
        }
        
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
	public static LabelNode getLastLabelNode(MethodNode method, boolean afterReturn)
	{
		AbstractInsnNode[] arr = method.instructions.toArray();
		boolean found = afterReturn;
		for(int i=arr.length-1;i>=0;i--)
		{
			AbstractInsnNode ab = arr[i];
			if(!found && isReturnOpcode(ab.getOpcode()))
				found = true;
			
			if(found && ab instanceof LabelNode)
			{
				return (LabelNode) ab;
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

}
