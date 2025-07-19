package jredfox.mce;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import jredfox.forgeversion.ForgeVersionProxy;

@Mod(modid="modclasseditor", name="Mod Class Editor @ALPHA", version="0.10.0", dependencies="after:*")
public class MCEMod {
	
	public MCEMod()
	{
		Transformer.batchLoad();
	}
	
	static
	{
		Test.load();//TODO: REMOVE
		ForgeVersionProxy.load();
		System.out.println(ForgeVersionProxy.getVersion() + " mc:" + ForgeVersionProxy.mcVersion);
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
