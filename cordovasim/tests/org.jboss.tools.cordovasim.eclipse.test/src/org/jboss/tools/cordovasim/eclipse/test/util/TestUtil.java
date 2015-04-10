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
package org.jboss.tools.cordovasim.eclipse.test.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public final class TestUtil {
	private static final String LOCALHOST_URL = "http://localhost"; //$NON-NLS-1$

	private TestUtil() {
	}

	public static String doHttpUrlConnection(int port, final String serverRelPath) throws IOException {
		String targetURL = LOCALHOST_URL + ":" + port + serverRelPath; //$NON-NLS-1$
		URL url = new URL(targetURL);
		URLConnection connection = url.openConnection();
		BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line;
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		br.close();
		return sb.toString();
	}

}
