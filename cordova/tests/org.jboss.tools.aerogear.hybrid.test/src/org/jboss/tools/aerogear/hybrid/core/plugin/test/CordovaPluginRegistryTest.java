package org.jboss.tools.aerogear.hybrid.core.plugin.test;

import java.util.List;

import org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPlugin;
import org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPluginInfo;
import org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPluginRegistryClient;
import org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPluginVersion;

import static org.junit.Assert.*;

import org.junit.Test;

public class CordovaPluginRegistryTest {
	
	@Test
	public void testRetrievePluginInfosFromCordovaRegistry(){
		CordovaPluginRegistryClient client = getCordovaIORegistryClient();
		List<CordovaPluginInfo> infos = client.retrievePluginInfos();
		assertNotNull(infos);
		assertFalse(infos.isEmpty());
		CordovaPluginInfo info = infos.get(0);
		assertNotNull(info.getName());
	}

	@Test
	public void testReadCordovaPluginFromCordovaRegistry(){
		CordovaPluginRegistryClient client = getCordovaIORegistryClient();
		List<CordovaPluginInfo> infos = client.retrievePluginInfos();
		CordovaPluginInfo info = infos.get(0);
		CordovaPlugin plugin = client.getCordovaPlugin(info.getName());
		assertNotNull(plugin);
		assertNotNull(plugin.getName());
		assertEquals(info.getName(), plugin.getName());
		assertNotNull(plugin.getVersions());
		List<CordovaPluginVersion> versions = plugin.getVersions();
		assertFalse(versions.isEmpty());
		CordovaPluginVersion version = versions.get(0);
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
