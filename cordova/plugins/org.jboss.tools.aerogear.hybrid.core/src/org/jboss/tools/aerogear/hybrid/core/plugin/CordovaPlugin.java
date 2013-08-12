package org.jboss.tools.aerogear.hybrid.core.plugin;

import java.util.ArrayList;
import java.util.List;

public class CordovaPlugin extends CordovaPluginInfo {
	
	private List<CordovaPluginVersion> versions;

	public List<CordovaPluginVersion> getVersions() {
		return versions;
	}

	public void addVersion(CordovaPluginVersion version ) {
		if(versions == null ){
			versions = new ArrayList<CordovaPluginVersion>();
		}
		versions.add(version);
	}
	
	
	

}
