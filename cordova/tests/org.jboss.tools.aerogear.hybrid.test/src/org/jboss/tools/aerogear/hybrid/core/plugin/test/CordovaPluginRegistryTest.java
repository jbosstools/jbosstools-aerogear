/*******************************************************************************
 * Copyright (c) 2013, 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *       Red Hat, Inc. - initial API and implementation
 *******************************************************************************/
package org.jboss.tools.aerogear.hybrid.core.plugin.test;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jboss.tools.aerogear.hybrid.core.plugin.registry.CordovaRegistryPlugin;
import org.jboss.tools.aerogear.hybrid.core.plugin.registry.CordovaRegistryPluginInfo;
import org.jboss.tools.aerogear.hybrid.core.plugin.registry.CordovaPluginRegistryManager;
import org.jboss.tools.aerogear.hybrid.core.plugin.registry.CordovaRegistryPluginVersion;

import static org.junit.Assert.*;

import org.junit.Test;

public class CordovaPluginRegistryTest {
	
	@Test
	public void testRetrievePluginInfosFromCordovaRegistry() throws CoreException{
		CordovaPluginRegistryManager client = getCordovaIORegistryClient();
		List<CordovaRegistryPluginInfo> infos = client.retrievePluginInfos(new NullProgressMonitor());
		assertNotNull(infos);
		assertFalse(infos.isEmpty());
		CordovaRegistryPluginInfo info = infos.get(0);
		assertNotNull(info.getName());
	}

	@Test
	public void testReadCordovaPluginFromCordovaRegistry() throws CoreException{
		CordovaPluginRegistryManager client = getCordovaIORegistryClient();
		List<CordovaRegistryPluginInfo> infos = client.retrievePluginInfos(new NullProgressMonitor());
		CordovaRegistryPluginInfo info = infos.get(0);
		CordovaRegistryPlugin plugin = client.getCordovaPluginInfo(info.getName());
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

	private CordovaPluginRegistryManager getCordovaIORegistryClient() {
		CordovaPluginRegistryManager client = new CordovaPluginRegistryManager("http://registry.cordova.io/");
		return client;
	}
}
