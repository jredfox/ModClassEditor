package jredfox.mce;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid="modclasseditor", name="Mod Class Editor", version="0.1", dependencies="after:*")
public class MCEMod {
	
	public static String stage = "preinit";
	
	@Mod.PreInit
	public void mcePreinit(FMLPreInitializationEvent e)
	{
		try
		{
			Class.forName("jredfox.mce.MCEGenInitPre");
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
		stage = "init";
	}
	
	@Mod.Init
	public void mceInit(FMLInitializationEvent e)
	{
		try
		{
			Class.forName("jredfox.mce.MCEGenInit");
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
		stage = "postinit";
	}
	
	@Mod.PostInit
	public void mcepostInit(FMLPostInitializationEvent e)
	{
		try
		{
			Class.forName("jredfox.mce.MCEGenInitPost");
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
	}

}	
