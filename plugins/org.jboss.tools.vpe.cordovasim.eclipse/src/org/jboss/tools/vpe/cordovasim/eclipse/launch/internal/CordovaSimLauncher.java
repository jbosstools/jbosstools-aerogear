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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.vpe.browsersim.browser.PlatformUtil;
import org.jboss.tools.vpe.browsersim.eclipse.launcher.BrowserSimLauncher;
import org.jboss.tools.vpe.browsersim.eclipse.launcher.ExternalProcessCallback;
import org.jboss.tools.vpe.browsersim.eclipse.launcher.ExternalProcessLauncher;
import org.jboss.tools.vpe.browsersim.util.BrowserSimUtil;

/**
 * @author "Yahor Radtsevich (yradtsevich)"
 */
public class CordovaSimLauncher {
	public static final String CORDOVASIM_CLASS_NAME = "org.jboss.tools.vpe.cordovasim.CordovaSimRunner"; //$NON-NLS-1$
	private static final List<ExternalProcessCallback> CORDOVASIM_CALLBACKS = BrowserSimLauncher.BROWSERSIM_CALLBACKS;
	private static final List<String> BUNDLES = new ArrayList<String>(); 
	static {
		BUNDLES.addAll(BrowserSimLauncher.BUNDLES);
		BUNDLES.addAll(Arrays.asList(
			"org.jboss.tools.vpe.cordovasim", //$NON-NLS-1$
			"org.jboss.tools.vpe.cordovasim.ripple", //$NON-NLS-1$
			"org.eclipse.jetty.continuation", //$NON-NLS-1$
			"org.eclipse.jetty.continuation", //$NON-NLS-1$
			"org.eclipse.jetty.http", //$NON-NLS-1$
			"org.eclipse.jetty.io", //$NON-NLS-1$
		    "org.eclipse.jetty.security", //$NON-NLS-1$
			"org.eclipse.jetty.server", //$NON-NLS-1$
			"org.eclipse.jetty.servlet", //$NON-NLS-1$
			"org.eclipse.jetty.util", //$NON-NLS-1$
		    "org.eclipse.jetty.client", //$NON-NLS-1$
		    "org.eclipse.jetty.servlets", //$NON-NLS-1$
		    "org.eclipse.jetty.rewrite", //$NON-NLS-1$
			"javax.servlet" //$NON-NLS-1$
		));
	}

	private static final List<String> RESOURCES_BUNDLES = BrowserSimLauncher.RESOURCES_BUNDLES;
	
	//if you change this parameter, see also @org.jbosstools.browsersim.ui.BrowserSim
	private static final String NOT_STANDALONE = BrowserSimLauncher.NOT_STANDALONE;	

	public static void launchCordovaSim(String projectString, String rootFolderString, String startPageString,
			Integer port) {
		List<String> parameters = new ArrayList<String>();
		parameters.add(NOT_STANDALONE);

		IContainer rootFolder = null;
		IProject project = null;
		if (projectString != null) {
			project = CordovaSimLaunchParametersUtil.getProject(projectString);
			
			if (rootFolderString != null) {
				rootFolder = CordovaSimLaunchParametersUtil.getRootFolder(project, rootFolderString);
			} else {
				rootFolder = CordovaSimLaunchParametersUtil.getDefaultRootFolder(project);
			}
		}
		
		String actualStartPageString = null;
		if (startPageString != null) {
			actualStartPageString = startPageString;
		} else {
			IResource startPage = CordovaSimLaunchParametersUtil.getDefaultStartPage(project, rootFolder);
			IPath startPagePath = CordovaSimLaunchParametersUtil.getRelativePath(rootFolder, startPage);
			if (startPagePath != null) {
				actualStartPageString = startPagePath.toString();
			}
		}
		
		if (rootFolder != null && actualStartPageString != null) {
			parameters.add(rootFolder.getLocation().toString());
			parameters.add(actualStartPageString);
			
			if (port != null) {
				parameters.add("-port"); //$NON-NLS-1$
				parameters.add(String.valueOf(port));
			}

			IVMInstall jvm = BrowserSimLauncher.getSelectedVM();
			
			String jvmPath = jvm.getInstallLocation().getAbsolutePath();
			String jrePath = jvm.getInstallLocation().getAbsolutePath() + File.separator + "jre";

			if (PlatformUtil.OS_LINUX.equals(PlatformUtil.getOs()) 
					|| (!BrowserSimUtil.isJavaFxAvailable(jvmPath) && !BrowserSimUtil.isJavaFxAvailable(jrePath))) {
				BUNDLES.add("org.jboss.tools.vpe.browsersim.javafx.mock"); //$NON-NLS-1$
			}
			
			ExternalProcessLauncher.launchAsExternalProcess(BUNDLES, RESOURCES_BUNDLES,
					CORDOVASIM_CALLBACKS, CORDOVASIM_CLASS_NAME, parameters, Messages.CordovaSimLauncher_CORDOVASIM, jvm);
		} else {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					MessageDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(),
							Messages.CordovaSimLauncher_CANNOT_RUN_CORDOVASIM, Messages.CordovaSimLauncher_CANNOT_FIND_ROOT_FOLDER);					
				}
			});
		}
	}
}
