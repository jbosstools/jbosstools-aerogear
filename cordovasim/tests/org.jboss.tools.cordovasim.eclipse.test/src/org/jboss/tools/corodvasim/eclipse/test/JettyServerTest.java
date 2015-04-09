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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.jboss.tools.cordovasim.eclipse.server.ServerCreator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
@RunWith(JUnit4.class)
public class JettyServerTest {
	private static final String MOCK_PATH = "MockPath"; //$NON-NLS-1$
	private static final String MOCK_PROJECT_NAME = "MockProjectName"; //$NON-NLS-1$
	private static final int PORT_MIN = 0;
	private static final int PORT_MAX = 65535;
	
	@Test
	public void createAndLaunchJettyServer() throws Exception {
		IPath path = mock(IPath.class);
		when(path.makeAbsolute()).thenReturn(path);
		when(path.toOSString()).thenReturn(MOCK_PATH);

		IProject project = mock(IProject.class);
		when(project.getName()).thenReturn(MOCK_PROJECT_NAME);

		IContainer container = mock(IContainer.class);
		when(container.getRawLocation()).thenReturn(path);
		when(container.getProjectRelativePath()).thenReturn(path);

		Server server = ServerCreator.createServer(project, container, null, new Integer(0));
		assertNotNull(server);
		assertEquals(Server.STOPPED, server.getState());

		server.start();

		ServerConnector connector = (ServerConnector) server.getConnectors()[0];
		assertNotNull(connector);

		int port = connector.getLocalPort();
		assertTrue(port > PORT_MIN && port <= PORT_MAX);

		assertEquals(Server.STARTED, server.getState());
		server.stop();
		assertEquals(Server.STOPPED, server.getState());
	}
}
