/*******************************************************************************
 * Copyright (c) 2007-2014 Red Hat, Inc.
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.tools.vpe.browsersim.eclipse.launcher.BrowserSimLauncher;
import org.jboss.tools.vpe.browsersim.eclipse.launcher.ExternalProcessCallback;
import org.jboss.tools.vpe.browsersim.eclipse.launcher.TransparentReader;
import org.jboss.tools.vpe.cordovasim.eclipse.launch.internal.CordovaSimLauncher;

/**
 * @author "Ilya Buziuk (ibuziuk)"
 */
public class CordovaSimRestartCallback implements ExternalProcessCallback{
	//if you change this parameter, see also @org.jbosstools.browsersim.ui.BrowserSim
	private static final String NOT_STANDALONE = BrowserSimLauncher.NOT_STANDALONE;	
	private static final String CORDOVASIM_RESTART_COMMAND = "org.jboss.tools.vpe.cordavasim.command.restart:"; //$NON-NLS-1$
	private static final String PARAMETER_DELIMITER = "_PARAMETER_DELIMITER_"; //$NON-NLS-1$

	
	@Override
	public String getCallbackId() {
		return CORDOVASIM_RESTART_COMMAND;
	}

	@Override
	public void call(String restartMessage, TransparentReader reader) throws IOException {
		restartMessage = restartMessage.trim();
		String[] messageArray = restartMessage.split(PARAMETER_DELIMITER);
		String[] parameterArray = Arrays.copyOfRange(messageArray, 1, messageArray.length); // removing CORDOVASIM_RESTART_COMMAND
		
		if (parameterArray != null) {
			List<String> parameters = new ArrayList<String>();
			parameters.add(NOT_STANDALONE);
			parameters.addAll(Arrays.asList(parameterArray));
			CordovaSimLauncher.launchCordovaSim(parameters);
		} else {
			throw new IllegalArgumentException("String '" + restartMessage + "' has invalid parameters"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

}