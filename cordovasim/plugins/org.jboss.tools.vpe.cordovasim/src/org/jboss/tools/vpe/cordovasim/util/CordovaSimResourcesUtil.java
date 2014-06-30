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
package org.jboss.tools.vpe.cordovasim.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.jboss.tools.vpe.cordovasim.CordovaSimLogger;
import org.jboss.tools.vpe.cordovasim.CustomBrowserSim;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public class CordovaSimResourcesUtil {
	private static final String RESOURCES_ROOT_FOLDER = "/org/jboss/tools/vpe/cordovasim/resources/";  //$NON-NLS-1$

	public static InputStream getResourceAsStream(String name) {
		if (name.startsWith("/")) { //$NON-NLS-1$
			return CustomBrowserSim.class.getResourceAsStream(name);
		} else {
			return CustomBrowserSim.class.getResourceAsStream(RESOURCES_ROOT_FOLDER + name);
		}
	}
	
	public static String getResourceAsString(String name) {
		InputStream input = getResourceAsStream(name);
		InputStreamReader reader = new InputStreamReader(input);
		BufferedReader bufferedReader = new BufferedReader(reader);

		StringBuilder stringBuilder = new StringBuilder();
		String read;
		try {
			try {
				while ((read = bufferedReader.readLine()) != null) {
					stringBuilder.append(read);
					stringBuilder.append('\n');
				}
			} finally {
				bufferedReader.close();
			}
		} catch (IOException e) {
			CordovaSimLogger.logError(e.getMessage(), e);
		}

		return stringBuilder.toString();
	}
		
}
