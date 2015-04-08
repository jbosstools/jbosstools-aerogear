package org.jboss.tools.corodvasim.eclipse.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.jboss.tools.browsersim.eclipse.launcher.BrowserSimLauncher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

@RunWith(JUnit4.class)
public class JettyBundlesTest {
	// Current version in TP is 9.2.5
	private static final String VERSION = "9.2.5.v20141112"; //$NON-NLS-1$

	@Test
	public void jettyVersionMatches() {
		List<String> bundles = getJettyBundles();
		for (String bundleName : bundles) {
			Bundle bundle = Platform.getBundle(bundleName);
			assertNotNull(bundle);
			Version version = bundle.getVersion();
			assertNotNull(version);
			assertEquals(VERSION, version.toString());
		}
	}

	private static List<String> getJettyBundles() {
		List<String> bundles = BrowserSimLauncher.getJettyBundles();
		bundles.addAll(Arrays.asList(
				"org.eclipse.jetty.client", //$NON-NLS-1$
				"org.eclipse.jetty.continuation", //$NON-NLS-1$
				"org.eclipse.jetty.proxy", //$NON-NLS-1$
				"org.eclipse.jetty.rewrite", //$NON-NLS-1$
				"org.eclipse.jetty.security", //$NON-NLS-1$
				"org.eclipse.jetty.server", //$NON-NLS-1$
				"org.eclipse.jetty.servlet", //$NON-NLS-1$
				"org.eclipse.jetty.servlets", //$NON-NLS-1$
				"org.eclipse.jetty.util", //$NON-NLS-1$
				"org.eclipse.jetty.http", //$NON-NLS-1$
				"org.eclipse.jetty.io", //$NON-NLS-1$
				"org.eclipse.jetty.websocket.api", //$NON-NLS-1$
				"org.eclipse.jetty.websocket.common", //$NON-NLS-1$
				"org.eclipse.jetty.websocket.servlet", //$NON-NLS-1$
				"org.eclipse.jetty.websocket.server" //$NON-NLS-1$
		));

		return bundles;
	}	
}
