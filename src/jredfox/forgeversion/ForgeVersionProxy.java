package jredfox.forgeversion;

import java.io.IOException;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import jredfox.mce.util.MCECoreUtils;

/**
 * Safely Get the Forge Version 1.3.2 - 1.12.2 without loading the ModContainer class
 * @author jredfox
 */
public class ForgeVersionProxy {
	
    //This number is incremented every time we remove deprecated code/major API changes, never reset
    public static int majorVersion;
    //This number is incremented every minecraft release, never reset
    public static int minorVersion;
    //This number is incremented every time a interface changes or new major feature is added, and reset every Minecraft version
    public static int revisionVersion;
    //This number is incremented every time Jenkins builds Forge, and never reset. Should always be 0 in the repo code.
    public static int buildVersion;
    // This is the minecraft version we're building for - used in various places in Forge/FML code
    public static String mcVersion;
    /**
     * If Forge build is < 11.14.3.1503 (MC 1.8) mcpVersion will be null
     */
    public static String mcpVersion;
    /**
     * Use this for ASM to determine if your transformer should use notch names vs SRG names
     */
    public static boolean notchNames;
	
	static
	{
		init();
	}
	
    public static int getMajorVersion()
    {
        return majorVersion;
    }

    public static int getMinorVersion()
    {
        return minorVersion;
    }

    public static int getRevisionVersion()
    {
        return revisionVersion;
    }

    public static int getBuildVersion()
    {
        return buildVersion;
    }

    public static String getVersion()
    {
        return String.format("%d.%d.%d.%d", majorVersion, minorVersion, revisionVersion, buildVersion);
    }

	public static void init() 
	{
		ClassNode c;
		try 
		{
			c = MCECoreUtils.getClassNode(ForgeVersionProxy.class.getClassLoader().getResourceAsStream("net/minecraftforge/common/ForgeVersion.class"));
			boolean modified = false;
			for(FieldNode f : c.fields)
			{
				String n = f.name;
				try
				{
					if(n.equals("majorVersion"))
						majorVersion = ((Number)f.value).intValue();
					else if(n.equals("minorVersion"))
						minorVersion = ((Number)f.value).intValue();
					else if(n.equals("revisionVersion"))
						revisionVersion = ((Number)f.value).intValue();
					else if(n.equals("buildVersion"))
						buildVersion = ((Number)f.value).intValue();
					else if(n.equals("mcVersion"))
						mcVersion = (String) f.value;
					else if(n.equals("mcpVersion"))
						mcpVersion = (String) f.value;
				}
				catch(NullPointerException e)
				{
					System.err.println("Field " + f.name + " is no longer final!");
					modified = true;
				}
			}
			
			//Handle ForgeVersion whan the class has been AT (Access Transformed) or modified and are no longer final fields
			if(modified)
			{
				MethodNode m = MCECoreUtils.getMethodNode(c, "<clinit>", "()V");
				for(AbstractInsnNode a : m.instructions.toArray())
				{
					if(a.getOpcode() == Opcodes.PUTSTATIC && a instanceof FieldInsnNode)
					{
						FieldInsnNode insn = (FieldInsnNode) a;
						if(insn.owner.equals("net/minecraftforge/common/ForgeVersion"))
						{
							String n = insn.name;
							if(n.equals("majorVersion"))
								majorVersion = MCECoreUtils.getIntFromInsn(insn.getPrevious());
							else if(n.equals("minorVersion"))
								minorVersion = MCECoreUtils.getIntFromInsn(insn.getPrevious());
							else if(n.equals("revisionVersion"))
								revisionVersion = MCECoreUtils.getIntFromInsn(insn.getPrevious());
							else if(n.equals("buildVersion"))
								buildVersion = MCECoreUtils.getIntFromInsn(insn.getPrevious());
							else if(n.equals("mcVersion"))
								mcVersion = MCECoreUtils.getStringFromInsn(insn.getPrevious());
							else if(n.equals("mcpVersion"))
								mcpVersion = MCECoreUtils.getStringFromInsn(insn.getPrevious());
						}
					}
				}
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
		
		initMcVersion();
		notchNames = majorVersion < 9 || majorVersion == 9 && minorVersion <= 11 && buildVersion < 937;
	}

	public static void initMcVersion() 
	{
		if(mcVersion != null)
			return;
		
		switch(majorVersion)
		{
			case 1:
				if(buildVersion < 30)
					mcVersion = "1.1";
				else
					mcVersion = "1.2.3";
			break;
			
			case 2:
				mcVersion = "1.2.4";
			break;
			
			case 3:
				mcVersion = "1.2.5";
			break;
			
			case 4:
				mcVersion = "1.3.2";
			break;
			
			case 5:
				mcVersion = "1.4";
			break;
			
			case 6:
				if(buildVersion <= 329)
					mcVersion = "1.4.1";
				else if(buildVersion <= 355)
					mcVersion = "1.4.2";
				else if(buildVersion <= 358)
					mcVersion = "1.4.3";
				else if(buildVersion <= 378)
					mcVersion = "1.4.4";
				else if(buildVersion <= 448)
					mcVersion = "1.4.5";
				else if(buildVersion <= 489)
					mcVersion = "1.4.6";
				else
					mcVersion = "1.4.7";
			break;
			
			case 7:
				if(buildVersion <= 598)
					mcVersion = "1.5";
				else if(buildVersion <= 682)
					mcVersion = "1.5.1";
				else
					mcVersion = "1.5.2";
			break;
			
			case 8:
				mcVersion = "1.6.1";
			break;
			
			case 9:
				if(buildVersion <= 871)
					mcVersion = "1.6.2";
				else if(minorVersion <= 11 && buildVersion <= 878)
					mcVersion = "1.6.3";
				else
					mcVersion = "1.6.4";
			break;
			
			case 10:
				if(minorVersion <= 12)
					mcVersion = "1.7.2";
				else
					mcVersion = "1.7.10";
			break;
			
			case 11:
				mcVersion = "1.8";
			break;
		
			default:
				break;
		}
	}

	public static void load() {}

}
