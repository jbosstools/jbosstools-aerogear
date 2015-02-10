package org.jboss.tools.corodvasim.eclipse.test;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.jboss.tools.vpe.browsersim.eclipse.launcher.BrowserSimLauncher;
import org.jboss.tools.vpe.browsersim.util.ManifestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.osgi.framework.Bundle;

@RunWith(JUnit4.class)
public class JettyBundlesTest {
	@Test
	public void jetty8Exists() {
		List<String> bundles = getJettyBundles();
		
		boolean bundleExists = false;
		String jettyVersion = ManifestUtil.getJettyVersion();
		String jettyMajorVersion = jettyVersion.substring(0, jettyVersion.indexOf(".")); //$NON-NLS-1$
		for (String bundleName : bundles) {
			Bundle[] jettys = Platform.getBundles(bundleName, null);
			for (Bundle jetty : jettys) {				
				if (jetty.getVersion().toString().startsWith(jettyMajorVersion)) {
					bundleExists = true;
				}
			}
			
			assertTrue(bundleName, bundleExists);
			bundleExists = false;
		}
	}
	
	private static List<String> getJettyBundles() {
		List<String> bundles = BrowserSimLauncher.getJettyBundles();
		bundles.addAll(Arrays.asList(
				"org.eclipse.jetty.client", //$NON-NLS-1$
				"org.eclipse.jetty.servlets", //$NON-NLS-1$
				"org.eclipse.jetty.rewrite" //$NON-NLS-1$
		));

		return bundles;
	}
}
