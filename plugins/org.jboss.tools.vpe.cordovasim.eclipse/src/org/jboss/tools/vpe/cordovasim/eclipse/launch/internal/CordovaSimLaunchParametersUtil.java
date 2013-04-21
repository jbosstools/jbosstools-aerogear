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
package org.jboss.tools.vpe.cordovasim.eclipse.launch.internal;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.jboss.tools.vpe.cordovasim.eclipse.Activator;

/**
 * @author "Yahor Radtsevich (yradtsevich)"
 */
public class CordovaSimLaunchParametersUtil {
	public static IContainer getRootFolder(String containerPath) {
		IContainer root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = getStartPage(root, containerPath);
		if (resource instanceof IContainer) {
			return (IContainer) resource;
		}
		return null;
	}

	public static IResource getStartPage(IContainer container, String path) {
		if (container != null && path != null && path.length() > 0) {
			return container.findMember(new Path(path));
		}
		return null;
	}

	public static IContainer validateAndGetRootFolder(String rootFolderString) throws CoreException {
		IContainer rootFolder = getRootFolder(rootFolderString);
		if (rootFolder == null || !rootFolder.exists()) {
			throw new CoreException(createErrorStatus("Root Folder path is not valid"));
		}
		return rootFolder;
	}
	
	public static IResource validateAndGetStartPage(IContainer rootFolder, String startPageString)
			throws CoreException {
		IResource startPage = getStartPage(rootFolder, startPageString);
		if (startPage == null || !startPage.exists()) {
			throw new CoreException(createErrorStatus("Start Page path is not valid"));
		}
		return startPage;
	}
	
	public static int validateAndGetPortNumber(String portString) throws CoreException {
		int port;
		try {
			port = Integer.parseInt(portString);//TODO: use an existing validator
			if (port < 1 || 65535 < port) {
				throw new CoreException(createErrorStatus("Port is invalid"));
			}
		} catch (NumberFormatException e) {
			throw new CoreException(createErrorStatus("Port is invalid"));
		}
		return port;
	}
	
	private static IStatus createErrorStatus(String message) {
		return new Status(IStatus.ERROR, Activator.PLUGIN_ID, message);
	}
}
