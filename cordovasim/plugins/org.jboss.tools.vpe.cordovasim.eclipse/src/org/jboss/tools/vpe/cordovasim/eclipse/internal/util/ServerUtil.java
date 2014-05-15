/*******************************************************************************
 * Copyright (c) 2007-2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.vpe.cordovasim.eclipse.internal.util;

import java.io.IOException;
import java.net.ServerSocket;

import org.jboss.tools.vpe.browsersim.eclipse.Activator;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public final class ServerUtil {

	private ServerUtil() {
	}

	public static boolean isPortAvailable(Integer port) {
		boolean isPortAvailable = true;
		if (port != null) { // if port == null jetty will use any free port
			ServerSocket socket = null;
			try {
				socket = new ServerSocket(port);
			} catch (IOException e) {
				isPortAvailable = false;
			} finally {
				if (socket != null)
					try {
						socket.close();
					} catch (IOException e) {
						Activator.logError(e.getMessage(), e);
					}
			}
		}
		return isPortAvailable;
	}

}
