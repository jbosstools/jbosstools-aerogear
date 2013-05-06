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
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.jboss.tools.vpe.cordovasim.eclipse.Activator;

/**
 * @author "Yahor Radtsevich (yradtsevich)"
 */
public class CordovaSimLaunchParametersUtil {
	private static final String AEROGEAR_HYBRID_NATURE_ID = "org.jboss.tools.aerogear.hybrid.core.HybridAppNature";
	private static final String ANDROID_NATURE_ID = "com.android.ide.eclipse.adt.AndroidNature";
	
	public static IProject validateAndGetProject(String projectString) throws CoreException {
		IProject project = getProject(projectString);
		if (project == null || !project.isOpen()) {
			throw new CoreException(createErrorStatus("Start Page path is not valid"));
		}
		return project;
	}
	
	public static IProject getProject(String projectString) {
		if (projectString != null) {
			try {
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectString);
				if (project.exists()) {
					return project;
				}
			} catch (IllegalArgumentException e) {
			}
		}
		
		return null;
	}
	
	public static IContainer getRootFolder(IProject project, String rootFolderString) {
		if (project != null && rootFolderString != null && rootFolderString.length() > 0) {
			IResource resource = project.findMember(new Path(rootFolderString));
			if (resource instanceof IContainer) {
				return (IContainer) resource;
			}
		}
		return null;
	}

	public static IResource getStartPage(IContainer container, String path) {
		if (container != null && path != null && path.length() > 0) {
			return container.findMember(new Path(path));
		}
		return null;
	}

	public static IContainer validateAndGetRootFolder(IProject project, String rootFolderString) throws CoreException {
		IContainer rootFolder = getRootFolder(project, rootFolderString);
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
	
	public static void validatePortNumber(String portString) throws CoreException {
		try {
			int port = Integer.parseInt(portString);//TODO: use an existing validator
			if (port < 1 || 65535 < port) {
				throw new CoreException(createErrorStatus("Port is invalid"));
			}
		} catch (NumberFormatException e) {
			throw new CoreException(createErrorStatus("Port is invalid"));
		}
	}
	
	private static IStatus createErrorStatus(String message) {
		return new Status(IStatus.ERROR, Activator.PLUGIN_ID, message);
	}

	public static IContainer getDefaultRootFolder(IProject project) {
		IContainer rootFolder = null;
		if (project != null) {
			try {
				if (project.hasNature(AEROGEAR_HYBRID_NATURE_ID)) {
					rootFolder = getRootFolder(project, "www");
				} else if (project.hasNature(ANDROID_NATURE_ID)) {
					rootFolder = getRootFolder(project, "assets/www");
				} else {
					rootFolder = project;
				}
			} catch (CoreException e) {
				Activator.logError(e.getMessage(), e);
			}
		}
		
		return rootFolder;
	}
	
	public static IResource getDefaultStartPage(IProject project, IContainer rootFolder) {
		// TODO: add config.xml parsing
		String startPageName = null;
//		try {
//			if (project.hasNature(AEROGEAR_HYBRID_NATURE_ID)) {
//				// TODO: startPageName = www/config.xml/widget/content/text()
//			} else if (project.hasNature(ANDROID_NATURE_ID)) {
//				// TODO: startPageName = res/xml/config.xml/widget/content/text()
//			}
//		} catch (CoreException e) {
//			Activator.logError(e.getMessage(), e);
//		}
		startPageName = "index.html";
		IResource startPage = getStartPage(rootFolder, startPageName);
		return startPage;
	}

	static IPath getRelativePath(IContainer container, IResource resource) {
		if (resource == null) {
			return null;
		}
		if (container == null) {
			return resource.getFullPath();
		}
	
		IPath containerPath = container.getFullPath();
		IPath resourcePath = resource.getFullPath();
	
		if (containerPath.isPrefixOf(resourcePath)) {
			int containerPathSegmentCount = containerPath.segmentCount();
			return resourcePath.removeFirstSegments(containerPathSegmentCount);			
		}
		return null;
	}
}
