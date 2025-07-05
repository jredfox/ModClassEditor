package jredfox.mce;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid="modclasseditor", name="Mod Class Editor @ALPHA", version="0.5.2", dependencies="after:*")
public class MCEMod {
	
	static
	{
		Test.load();//TODO: REMOVE
	}
	
	@Mod.PreInit
	public void mcePreinit(FMLPreInitializationEvent e)
	{
		if(Transformer.gen == null)
			throw new RuntimeException("Mod Class Editor Must be thrown into the \"coremods\" folder!");
		Transformer.gen.stage = "preInit";
		MCEGenInitPre.init();
	}
	
	@Mod.Init
	public void mceInit(FMLInitializationEvent e)
	{
		Transformer.gen.stage = "init";
		MCEGenInit.init();
	}
	
	@Mod.PostInit
	public void mcepostInit(FMLPostInitializationEvent e)
	{
		Transformer.gen.stage = "postInit";
		MCEGenInitPost.init();
	}

}	
