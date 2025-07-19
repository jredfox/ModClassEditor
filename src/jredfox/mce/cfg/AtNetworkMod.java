package jredfox.mce.cfg;

import org.ralleytn.simple.json.JSONObject;

import jredfox.mce.util.MCEUtil;

public class AtNetworkMod {

	public boolean clientSideRequired;
	public boolean serverSideRequired;
	public String versionBounds = "";
	
	public boolean hasCS;
	public boolean hasSS;
	public boolean hasVB;
	
	public AtNetworkMod() {}
	
	public AtNetworkMod(JSONObject j)
	{
		if(j.containsKey("clientSideRequired"))
		{
			this.clientSideRequired = MCEUtil.parseBoolean(j.getAsString("clientSideRequired"));
			this.hasCS = true;
		}
		if(j.containsKey("serverSideRequired"))
		{
			this.serverSideRequired = MCEUtil.parseBoolean(j.getAsString("serverSideRequired"));
			this.hasSS = true;
		}
		if(j.containsKey("versionBounds"))
		{
			this.versionBounds = j.getAsString("versionBounds").trim();
			this.hasVB = true;
		}
	}
}
