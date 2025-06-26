package jredfox.mce;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class Plugin implements IFMLLoadingPlugin {

	public static boolean isObf = true;

	@Override
	public void injectData(Map<String, Object> data) {
		isObf = (Boolean) data.get("runtimeDeobfuscationEnabled");
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[]{"jredfox.mce.Transformer"};
	}

	@Override
	public String[] getLibraryRequestClass() {
		return null;
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

}
