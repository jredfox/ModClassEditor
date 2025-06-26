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
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InnerClassNode;
import org.objectweb.asm.tree.MethodNode;
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
	
	public static byte[] toByteArray(ClassWriter classWriter, String transformedName) throws IOException 
	{
        byte[] bytes = classWriter.toByteArray();
        if(Boolean.parseBoolean(System.getProperty("asm.dump", "false")))
        	dumpFile(transformedName, bytes);
        
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
	
	/**
	 * dumps a file from memory
	 * @throws IOException 
	 */
	public static void dumpFile(String name, byte[] bytes) throws IOException
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

}
