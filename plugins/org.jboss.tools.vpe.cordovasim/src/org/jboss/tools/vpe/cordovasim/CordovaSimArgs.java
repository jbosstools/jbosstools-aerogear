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
package org.jboss.tools.vpe.cordovasim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.tools.vpe.browsersim.BrowserSimArgs;
import org.jboss.tools.vpe.browsersim.BrowserSimLogger;

/**
 * @author Yahor Radtsevich (yradtsevich)
 */
public class CordovaSimArgs {
	private static final int DEFAULT_PORT = 0;// any free port
	
	private static String rootFolder;
	private static String startPage;
	private static int port;
	public static boolean standalone;

	public static void parseArgs(String[] args) {
		List<String> params = new ArrayList<String>(Arrays.asList(args));
		standalone = !params.contains(BrowserSimArgs.NOT_STANDALONE);
		if (!standalone) {
			params.remove(BrowserSimArgs.NOT_STANDALONE);
		}
		
		int portParameterIndex = params.indexOf("-port"); //$NON-NLS-1$
		if (portParameterIndex >= 0) {
			params.remove(portParameterIndex);
			try {
				port = Integer.parseInt(params.remove(portParameterIndex));
			} catch (NumberFormatException e) {
				BrowserSimLogger.logError("Incorrect port value", e); //$NON-NLS-1$
				port = DEFAULT_PORT;
			}
		} else {
			port = DEFAULT_PORT;
		}
		
		if (params.size() > 0) {
			startPage = params.remove(params.size() - 1); // the last parameter
		} else {
			startPage = ""; //$NON-NLS-1$
		}
		
		if (params.size() > 0) {
			rootFolder = params.remove(params.size() - 1); // the parameter before the last one
		} else {
			rootFolder = "."; //$NON-NLS-1$
		}
	}

	public static String getRootFolder() {
		return rootFolder;
	}

	public static String getStartPage() {
		return startPage;
	}

	public static int getPort() {
		return port;
	}
	
	public static void setPort(int port) {
		CordovaSimArgs.port = port;
	}

	public static boolean isStandalone() {
		return standalone;
	}
}
