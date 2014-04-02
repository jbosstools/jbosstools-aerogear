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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.jboss.tools.aerogear.hybrid.core.HybridProject;
import org.jboss.tools.aerogear.hybrid.core.engine.HybridMobileEngine;
import org.jboss.tools.aerogear.hybrid.core.engine.HybridMobileLibraryResolver;
import org.jboss.tools.aerogear.hybrid.core.engine.PlatformLibrary;
import org.jboss.tools.vpe.browsersim.browser.PlatformUtil;
import org.jboss.tools.vpe.cordovasim.eclipse.Activator;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * @author "Yahor Radtsevich (yradtsevich)"
 * @author "Ilya Buziuk (ibuziuk)"
 */
public class CordovaSimLaunchParametersUtil {
	private static final String AEROGEAR_HYBRID_NATURE_ID = "org.jboss.tools.aerogear.hybrid.core.HybridAppNature"; //$NON-NLS-1$
	private static final String ANDROID_NATURE_ID = "com.android.ide.eclipse.adt.AndroidNature"; //$NON-NLS-1$
	private static final String ANDROID_PLATFORM_ID = "android"; //$NON-NLS-1$
	private static final String IOS_PLATFORM_ID = "ios"; //$NON-NLS-1$
			
	public static IProject validateAndGetProject(String projectString) throws CoreException {
		IProject project = getProject(projectString);
		if (project == null || !project.isOpen()) {
			throw new CoreException(createErrorStatus(Messages.CordovaSimLaunchParametersUtil_INVALID_START_PAGE_PATH));
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
			throw new CoreException(createErrorStatus(Messages.CordovaSimLaunchParametersUtil_INVALID_ROOT_FOLDER_PATH));
		}
		return rootFolder;
	}
	
	public static IResource validateAndGetStartPage(IContainer rootFolder, String startPageString)
			throws CoreException {
		IResource startPage = getStartPage(rootFolder, startPageString);
		if (startPage == null || !startPage.exists()) {
			throw new CoreException(createErrorStatus(Messages.CordovaSimLaunchParametersUtil_INVALID_START_PAGE_PATH)); 
		}
		return startPage;
	}
	
	public static void validatePortNumber(String portString) throws CoreException {
		try {
			int port = Integer.parseInt(portString);//TODO: use an existing validator
			if (port < 1 || 65535 < port) {
				throw new CoreException(createErrorStatus(Messages.CordovaSimLaunchParametersUtil_INVALID_PORT));
			}
		} catch (NumberFormatException e) {
			throw new CoreException(createErrorStatus(Messages.CordovaSimLaunchParametersUtil_INVALID_PORT)); 
		}
	}
	
	private static IStatus createErrorStatus(String message) {
		return new Status(IStatus.ERROR, Activator.PLUGIN_ID, message);
	}

	public static IContainer getDefaultRootFolder(IProject project) {
		IContainer rootFolder = null;
		if (project != null && project.isOpen()) {
			try {
				if (project.hasNature(AEROGEAR_HYBRID_NATURE_ID)) {
					rootFolder = getRootFolder(project, "www"); //$NON-NLS-1$
				} else if (project.hasNature(ANDROID_NATURE_ID)) {
					rootFolder = getRootFolder(project, "assets/www"); //$NON-NLS-1$
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
		String startPageName = null;
		if (project != null && project.isOpen()) {
			try {
				String configFilePath = null;
				if (project.hasNature(AEROGEAR_HYBRID_NATURE_ID)) {
					configFilePath = "www/config.xml"; //$NON-NLS-1$
				} else if (project.hasNature(ANDROID_NATURE_ID)) {
					configFilePath = "res/xml/config.xml"; //$NON-NLS-1$
				}
				if (configFilePath != null) {
					IResource configResource = project.findMember("www/config.xml"); //$NON-NLS-1$
					if (configResource instanceof IFile) {
						IFile configFile = (IFile) configResource;
						startPageName = getStartPageName(configFile);
					}
				}
			} catch (CoreException e) {
				Activator.logError(e.getMessage(), e);
			}
		}
		
		if (startPageName == null) {
			startPageName = "index.html"; // standard default value //$NON-NLS-1$
		}
		IResource startPage = getStartPage(rootFolder, startPageName);
		return startPage;
	}
	
	
	/**
	 * Returns the location of the cordova.js file of the {@link HybridProject}.
	 * 
	 * Returns {@code null} if it is not found.
	 */
	public static String getCordovaEngineLocation(IProject project) {
		HybridProject hybridProject = HybridProject.getHybridProject(project);
		if (hybridProject != null) {
			HybridMobileEngine activeEngine = hybridProject.getActiveEngine();
			if (activeEngine != null) {
				PlatformLibrary platformLibrary = getPlatformLibrary(activeEngine);
				if (platformLibrary != null) {
					HybridMobileLibraryResolver platformLibraryResolver = platformLibrary.getPlatformLibraryResolver();
					if (platformLibraryResolver != null) {
						URL templateFile = platformLibraryResolver.getTemplateFile(HybridMobileLibraryResolver.PATH_CORDOVA_JS);
						if (templateFile != null) {
							return templateFile.getFile(); // cordova.js location
						}
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns the version of the engine of the {@link HybridProject}.
	 * 
	 * Returns {@code null} if it is not found.
	 */
	public static String getCordovaVersion(IProject project) {
		HybridProject hybridProject = HybridProject.getHybridProject(project);
		if (hybridProject != null) {
			HybridMobileEngine activeEngine = hybridProject.getActiveEngine();
			if (activeEngine != null) {
				return activeEngine.getVersion();
			}
		}
		return null;
	}
	
	private static PlatformLibrary getPlatformLibrary(HybridMobileEngine engine) {
		PlatformLibrary pl = null;
		if (engine != null) {
			pl = engine.getPlatformLib(ANDROID_PLATFORM_ID); // Using android by default
			if (pl == null && PlatformUtil.OS_MACOSX.equals(PlatformUtil.getOs())) {
				pl = engine.getPlatformLib(IOS_PLATFORM_ID);
			}
		}
		return pl;
	}
	
	/**
	 * Reads PhoneGap's config.xml and tries to extract the start page name from it.
	 * 
	 * Returns {@code null} if it is not found.
	 */
	private static String getStartPageName(IFile configFile) {
		String startPageName = null;
		InputStream inputStream = null;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			inputStream = configFile.getContents();
			Document document = dBuilder.parse(inputStream);

			// optional, but recommended
			// see http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			document.getDocumentElement().normalize();
			XPath xPath = XPathFactory.newInstance().newXPath();
			XPathExpression xPathExpression = xPath.compile("//widget/content/@src"); //$NON-NLS-1$
			Node startPageNameNode = (Node) xPathExpression.evaluate(document, XPathConstants.NODE);
			if (startPageNameNode != null) {
				startPageName = startPageNameNode.getNodeValue().trim();
			}
		} catch (SAXException e) {
			// This may happen if user has a not valid XML. We just ignore this.
		} catch (IOException e) {
			Activator.logError(e.getMessage(), e);
		} catch (CoreException e) {
			Activator.logError(e.getMessage(), e);
		} catch (ParserConfigurationException e) {
			Activator.logError(e.getMessage(), e);
		} catch (XPathExpressionException e) {
			Activator.logError(e.getMessage(), e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					Activator.logError(e.getMessage(), e);
				}
			}
		}

		return startPageName;
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
