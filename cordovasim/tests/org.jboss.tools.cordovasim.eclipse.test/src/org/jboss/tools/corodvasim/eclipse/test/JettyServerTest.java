/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.corodvasim.eclipse.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.jboss.tools.cordovasim.eclipse.server.ServerCreator;
import org.jboss.tools.cordovasim.eclipse.test.util.TestUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
@RunWith(JUnit4.class)
public class JettyServerTest {
	private static final String APACHE_LICENSE = "Apache Software Foundation (ASF)"; //$NON-NLS-1$
	private static final String CORDOVA_JS_BUILD_LABEL = "CORDOVA_JS_BUILD_LABEL"; //$NON-NLS-1$
	private static final String CORDOVA_JS = "/cordova.js"; //$NON-NLS-1$
	private static final String CORDOVA_PLUGINS_JS = "/cordova_plugins.js"; //$NON-NLS-1$
	private static final String CORDOVA_PLUGINS_LIST = "cordova.define('cordova/plugin_list', function(require, exports, module) {  module.exports = [module.exports = []]});"; //$NON-NLS-1$
	private static final String CONFIG_XML = "/config.xml"; //$NON-NLS-1$
	private static final String DOCTYPE_HTML = "<!DOCTYPE html>"; //$NON-NLS-1$
	private static final String EXAMPLE_COM = "http://www.example.com/"; //$NON-NLS-1$
	private static final String EXAMPLE_DOMAIN = "Example Domain"; //$NON-NLS-1$	
	private static final String INDEX_HTML = "index.html"; //$NON-NLS-1$
	private static final String LOCAL_PROXY = "/ripple/xhr_proxy"; //$NON-NLS-1$
	private static final String MOCK_PATH = "MockPath"; //$NON-NLS-1$
	private static final String MOCK_PROJECT_NAME = "MockProjectName"; //$NON-NLS-1$
	private static final String OK = "OK"; //$NON-NLS-1$
	private static final String RIPPLE = "Ripple Mobile Environment Emulator"; //$NON-NLS-1$
	private static final String RIPPLE_ASSETS = "/ripple/assets/"; //$NON-NLS-1$
	private static final String RIPPLE_CSS = "ripple.css"; //$NON-NLS-1$
	private static final String RIPPLE_JS = "ripple.js"; //$NON-NLS-1$
	private static final String PROXY_TO_PARAMETER = "tinyhippos_rurl"; //$NON-NLS-1$
	private static final String RIPPLE_USER_AGENT = "/ripple/user-agent"; //$NON-NLS-1$
	private static final int PORT_MIN = 0;
	private static final int PORT_MAX = 65535;

	private static Server server;
	private static int port;

	@BeforeClass
	public static void createAndLaunchJettyServe() throws Exception {
		IPath path = mock(IPath.class);
		when(path.makeAbsolute()).thenReturn(path);
		when(path.toOSString()).thenReturn(MOCK_PATH);

		IProject project = mock(IProject.class);
		when(project.getName()).thenReturn(MOCK_PROJECT_NAME);

		IContainer container = mock(IContainer.class);
		when(container.getRawLocation()).thenReturn(path);
		when(container.getProjectRelativePath()).thenReturn(path);

		server = ServerCreator.createServer(project, container, null, new Integer(0));
		server.start();

		ServerConnector connector = (ServerConnector) server.getConnectors()[0];
		port = connector.getLocalPort();
	}

	@Test
	public void testServerState() {
		assertNotNull(server);
		assertEquals(Server.STARTED, server.getState());
	}

	@Test
	public void testServerPort() {
		assertTrue(port > PORT_MIN && port <= PORT_MAX);
	}

	@Test
	public void testUserAgentServlet() throws IOException {
		String response = TestUtil.doHttpUrlConnection(port, RIPPLE_USER_AGENT);
		assertNotNull(response);
		assertEquals(response, OK);
	}

	@Test
	public void testCordovaJsServlet() throws IOException {
		String response = TestUtil.doHttpUrlConnection(port, CORDOVA_JS);
		assertNotNull(response);
		assertTrue(response.contains(CORDOVA_JS_BUILD_LABEL));
	}

	@Test
	public void testProxyServlet() throws IOException {
		String serverRelPath = LOCAL_PROXY + "?" + PROXY_TO_PARAMETER + "=" + EXAMPLE_COM; //$NON-NLS-1$//$NON-NLS-2$
		String response = TestUtil.doHttpUrlConnection(port, serverRelPath);
		assertNotNull(response);
		assertTrue(response.contains(EXAMPLE_DOMAIN));
	}

	@Test(expected = FileNotFoundException.class)
	public void testConfigXMLServlet() throws IOException {
		TestUtil.doHttpUrlConnection(port, CONFIG_XML);
	}

	@Test
	public void testCordovaPluginsJSServlet() throws IOException {
		String response = TestUtil.doHttpUrlConnection(port, CORDOVA_PLUGINS_JS);
		assertNotNull(response);
		assertTrue(response.contains(CORDOVA_PLUGINS_LIST));
	}

	@Test
	public void testRippleJsExistence() throws IOException {
		String response = TestUtil.doHttpUrlConnection(port, RIPPLE_ASSETS + RIPPLE_JS);
		assertNotNull(response);
		assertTrue(response.contains(RIPPLE));
	}

	@Test
	public void testRippleCssExistence() throws IOException {
		String response = TestUtil.doHttpUrlConnection(port, RIPPLE_ASSETS + RIPPLE_CSS);
		assertNotNull(response);
		assertTrue(response.contains(APACHE_LICENSE));
	}

	@Test
	public void testRippleIndexHTMLExistence() throws IOException {
		String response = TestUtil.doHttpUrlConnection(port, RIPPLE_ASSETS + INDEX_HTML);
		assertNotNull(response);
		assertTrue(response.contains(DOCTYPE_HTML));
	}

	@AfterClass
	public static void stopJettyServer() throws Exception {
		server.stop();
	}

}
