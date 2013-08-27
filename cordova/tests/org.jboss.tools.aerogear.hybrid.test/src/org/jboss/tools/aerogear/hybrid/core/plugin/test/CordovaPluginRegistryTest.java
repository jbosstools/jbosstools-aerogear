package org.jboss.tools.aerogear.hybrid.core.plugin.test;

import java.util.List;

import org.jboss.tools.aerogear.hybrid.core.plugin.registry.CordovaRegistryPlugin;
import org.jboss.tools.aerogear.hybrid.core.plugin.registry.CordovaRegistryPluginInfo;
import org.jboss.tools.aerogear.hybrid.core.plugin.registry.CordovaPluginRegistryClient;
import org.jboss.tools.aerogear.hybrid.core.plugin.registry.CordovaRegistryPluginVersion;

import static org.junit.Assert.*;

import org.junit.Test;

public class CordovaPluginRegistryTest {
	
	@Test
	public void testRetrievePluginInfosFromCordovaRegistry(){
		CordovaPluginRegistryClient client = getCordovaIORegistryClient();
		List<CordovaRegistryPluginInfo> infos = client.retrievePluginInfos();
		assertNotNull(infos);
		assertFalse(infos.isEmpty());
		CordovaRegistryPluginInfo info = infos.get(0);
		assertNotNull(info.getName());
	}

	@Test
	public void testReadCordovaPluginFromCordovaRegistry(){
		CordovaPluginRegistryClient client = getCordovaIORegistryClient();
		List<CordovaRegistryPluginInfo> infos = client.retrievePluginInfos();
		CordovaRegistryPluginInfo info = infos.get(0);
		CordovaRegistryPlugin plugin = client.getCordovaPlugin(info.getName());
		assertNotNull(plugin);
		assertNotNull(plugin.getName());
		assertEquals(info.getName(), plugin.getName());
		assertNotNull(plugin.getVersions());
		List<CordovaRegistryPluginVersion> versions = plugin.getVersions();
		assertFalse(versions.isEmpty());
		CordovaRegistryPluginVersion version = versions.get(0);
		assertNotNull(version.getName());
		assertNotNull(version.getVersionNumber());
		assertNotNull(version.getDistributionSHASum());
		assertNotNull(version.getDistributionTarball());
	}

	private CordovaPluginRegistryClient getCordovaIORegistryClient() {
		CordovaPluginRegistryClient client = new CordovaPluginRegistryClient("http://registry.cordova.io/");
		return client;
	}
}
