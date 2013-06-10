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

public class CordovaSimArgs {
	private static final String DEFAULT_PORT = "0";// any free port
	private String rootFolder;
	private String startPage;
	private String port;
	private boolean standalone;
	
	public CordovaSimArgs(String rootFolder, String startPage, String port, boolean standalone) {
		this.rootFolder = rootFolder;
		this.startPage = startPage;
		this.port = port;
		this.standalone = standalone;
	}

	public static CordovaSimArgs parseArgs(String[] args) {
		List<String> params = new ArrayList<String>(Arrays.asList(args));
		boolean notStandalone = params.contains(BrowserSimArgs.NOT_STANDALONE);
		if (notStandalone) {
			params.remove(BrowserSimArgs.NOT_STANDALONE);
		}
		
		int portParameterIndex = params.indexOf("-port");
		String port;
		if (portParameterIndex >= 0) {
			params.remove(portParameterIndex);
			port = params.remove(portParameterIndex);
		} else {
			port = DEFAULT_PORT;
		}
		String startPage;
		if (params.size() > 0) {
			startPage = params.remove(params.size() - 1); // the last parameter
		} else {
			startPage = "";
		}
		
		String rootFolder;
		if (params.size() > 0) {
			rootFolder = params.remove(params.size() - 1); // the parameter before the last one
		} else {
			rootFolder = ".";
		}	

		return new CordovaSimArgs(rootFolder, startPage, port, !notStandalone);
	}

	public String getRootFolder() {
		return rootFolder;
	}

	public String getStartPage() {
		return startPage;
	}

	public String getPort() {
		return port;
	}

	public boolean isStandalone() {
		return standalone;
	}
}
