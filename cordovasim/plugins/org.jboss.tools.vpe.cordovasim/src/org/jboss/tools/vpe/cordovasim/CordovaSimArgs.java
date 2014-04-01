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

import java.io.IOException;
import java.net.ServerSocket;
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
	private static String cordovaEngineLocation;
	private static String cordovaVersion;
	private static int port;
	private static boolean restartRequired;

	public static void parseArgs(String[] args) {
		List<String> params = new ArrayList<String>(Arrays.asList(args));
		BrowserSimArgs.standalone = !params.contains(BrowserSimArgs.NOT_STANDALONE);
		if (!BrowserSimArgs.standalone) {
			params.remove(BrowserSimArgs.NOT_STANDALONE);
		}
	
		restartRequired = false;
		
		int versionParameterIndex = params.indexOf("-version"); //$NON-NLS-1$
		if (versionParameterIndex >= 0) {
			params.remove(versionParameterIndex);
			cordovaVersion = params.remove(versionParameterIndex);
		} else {
			cordovaVersion = "3.1.0";  //$NON-NLS-1$ Using cordova-3.1.0.js
		}

		int engineParameterIndex = params.indexOf("-engine"); //$NON-NLS-1$
		if (engineParameterIndex >= 0) {
			params.remove(engineParameterIndex);
			cordovaEngineLocation = params.remove(engineParameterIndex);
		} else {
			cordovaEngineLocation = null;
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

		try {
			ServerSocket socket = new ServerSocket(0);
			BrowserSimArgs.debuggerPort = socket.getLocalPort();
			socket.close();
		} catch (IOException e) {
			CordovaSimLogger.logError(e.getMessage(), e);
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
	
	public static String getCordovaEngineLocation() {
		return cordovaEngineLocation;
	}
	
	public static String getCordovaVersion() {
		return cordovaVersion;
	}
	
	public static void setPort(int port) {
		CordovaSimArgs.port = port;
	}

	public static boolean isRestartRequired() {
		return restartRequired;
	}

	public static void setRestartRequired(boolean restartRequired) {
		CordovaSimArgs.restartRequired = restartRequired;
	}

}
