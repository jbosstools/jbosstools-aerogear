/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cordavasim.eclipse.callbacks;

import java.io.IOException;

import org.eclipse.jetty.server.Server;
import org.jboss.tools.vpe.browsersim.eclipse.Activator;
import org.jboss.tools.vpe.browsersim.eclipse.launcher.ExternalProcessCallback;
import org.jboss.tools.vpe.browsersim.eclipse.launcher.TransparentReader;
import org.jboss.tools.vpe.cordovasim.eclipse.server.internal.ServerStorage;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public class StopServerCallback implements ExternalProcessCallback {
	private static final String STOP_SERVER_COMMAND = "org.jboss.tools.vpe.cordavasim.command.stop.server:"; //$NON-NLS-1$
	
	@Override
	public String getCallbackId() {
		return STOP_SERVER_COMMAND;
	}

	@Override
	public void call(String lastString, TransparentReader reader) throws IOException {
		String portString = lastString.replaceAll("\\D+","");  //$NON-NLS-1$//$NON-NLS-2$ - leaving only digits 
		int port = Integer.parseInt(portString);
		Server server = ServerStorage.getStorage().get(port);
		ServerStorage.getStorage().remove(port);
		if (server != null) {
			try {
				server.stop();
				server.join();
			} catch (Exception e) {
				Activator.logError(e.getMessage(), e);
			}
		}
	}

}
