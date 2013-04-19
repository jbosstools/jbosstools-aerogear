/*******************************************************************************
 * Copyright (c) 2007-2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.vpe.cordovasim.eclipse.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.osgi.framework.internal.core.BundleFragment;
import org.eclipse.osgi.framework.internal.core.BundleHost;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.vpe.browsersim.browser.PlatformUtil;
import org.jboss.tools.vpe.cordovasim.eclipse.Activator;
import org.jboss.tools.vpe.cordovasim.eclipse.callbacks.CordovaSimCallback;
import org.jboss.tools.vpe.cordovasim.eclipse.callbacks.OpenFileCallback;
import org.jboss.tools.vpe.cordovasim.eclipse.callbacks.ViewSourceCallback;
import org.osgi.framework.Bundle;

/**
 * @author "Yahor Radtsevich (yradtsevich)"
 */
@SuppressWarnings("restriction")
public class CordovaSimLauncher {
	public static final String CORDOVASIM_CLASS_NAME = "org.jboss.tools.vpe.cordovasim.CordovaSimRunner"; //$NON-NLS-1$
	private static final CordovaSimCallback[] BROWSERSIM_CALLBACKS = { new ViewSourceCallback(), new OpenFileCallback() };
	private static final String[] REQUIRED_BUNDLES = {
		"org.jboss.tools.vpe.browsersim",
		"org.jboss.tools.vpe.cordovasim",
		"org.jboss.tools.vpe.browsersim.browser",
		"org.eclipse.swt",
		"org.eclipse.jetty.continuation",
		"org.eclipse.jetty.http",
		"org.eclipse.jetty.io",
		"org.eclipse.jetty.security",
		"org.eclipse.jetty.server",
		"org.eclipse.jetty.servlet",
		"org.eclipse.jetty.util",
		"org.eclipse.jetty.client",
		"org.eclipse.jetty.servlets",
		"org.eclipse.jetty.rewrite",
		"javax.servlet"
	};
	private static final String[] OPTIONAL_BUNDLES = {		
		// org.eclipse.swt plugin may contain this fragment in itself - that is why it is optional. See JBIDE-11923
		"org.eclipse.swt." + PlatformUtil.CURRENT_PLATFORM 
	};
	//if you change this parameter, see also @org.jbosstools.browsersim.ui.BrowserSim
	private static final String NOT_STANDALONE = "-not-standalone"; //$NON-NLS-1$
	

	public static void launchCordovaSim(String initialUrl) {
		try {
			
			String classPath = getClassPathString();
			String javaCommand = System.getProperty("java.home") + "/bin/java"; //$NON-NLS-1$ //$NON-NLS-2$
			
			// This is a workaround for JDK 7: JBIDE-12467 Unable to Run Browsersim in Windows7 64b + JRE7 32b
			// On Windows and Java 7 the 'java.home' variable always points to JRE, but 'eclipse.vm' may point to JDK,
			// if it is specified explicitly in the inclipse.ini.
			boolean isJava1_7 = "1.7".equals(System.getProperty("java.specification.version")); //$NON-NLS-1$ //$NON-NLS-2$
			if (Platform.OS_WIN32.equals(Platform.getOS()) && isJava1_7) {
				String eclipseVm = System.getProperty("eclipse.vm");
				if (eclipseVm != null) {
					if (eclipseVm.endsWith("java") || eclipseVm.endsWith("java.exe") 
							|| eclipseVm.endsWith("javaw") || eclipseVm.endsWith("javaw.exe")) {
						javaCommand = eclipseVm;
					}
				}
			}
			
			List<String> commandElements = new ArrayList<String>();
			commandElements.add(javaCommand);
			
			if (Platform.OS_MACOSX.equals(Platform.getOS())) {
				commandElements.add("-XstartOnFirstThread"); //$NON-NLS-1$
				if (Platform.ARCH_X86.equals(Platform.getOSArch())) {
					commandElements.add("-d32"); //$NON-NLS-1$
				}
			}
			
			commandElements.add("-cp"); //$NON-NLS-1$
			commandElements.add(classPath);
			commandElements.add(CORDOVASIM_CLASS_NAME);
			
			//optional parameters
			commandElements.add(NOT_STANDALONE);
			File file = null;
			if (initialUrl != null) {
				try {
					file = new File(new URI(initialUrl));
				} catch (URISyntaxException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
				if (file != null) {
					commandElements.add(file.toString());
				}
			}
			for (String c : commandElements) {
				System.out.println(c + " ");
			}
			ProcessBuilder processBuilder = new ProcessBuilder(commandElements);
			processBuilder.directory(FileLocator.getBundleFile(Platform.getBundle("org.jboss.tools.vpe.cordovasim"))); // еее
			
			Process browserSimProcess = processBuilder.start();
			final IWorkbenchListener browserSimPostShutDownDestroyer = new CordovaSimPostShutDownDestroyer(browserSimProcess);
			PlatformUI.getWorkbench().addWorkbenchListener(browserSimPostShutDownDestroyer);
			
			final InputStreamReader errorReader = new InputStreamReader(browserSimProcess.getErrorStream());
			final Reader inputReader = new InputStreamReader(browserSimProcess.getInputStream());
			new Thread() {
				public void run() {
					try {
						TransparentReader transparentReader = new TransparentReader(inputReader, System.out);
						String nextLine;
						while ((nextLine = transparentReader.readLine(true)) != null) {
							for (CordovaSimCallback callback : BROWSERSIM_CALLBACKS) { 
								if (nextLine.startsWith(callback.getCallbackId())) {
									callback.call(nextLine, transparentReader);
								}
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}  finally {
						PlatformUI.getWorkbench().removeWorkbenchListener(browserSimPostShutDownDestroyer);
					}
				};
			}.start();
			new Thread() {
				public void run() {
					int nextCharInt;
					try {
						while ((nextCharInt = errorReader.read()) >= 0) {
							System.err.print((char) nextCharInt);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				};
			}.start();
		} catch (IOException e) {
			Activator.logError(e.getMessage(), e);
		}
	}

	private static String getClassPathString() throws IOException {
		List<Bundle> classPathBundles = new ArrayList<Bundle>();
		for (String bundleName : REQUIRED_BUNDLES) {
			Bundle bundle = Platform.getBundle(bundleName);
			if (bundle != null) {
				classPathBundles.add(bundle);
			} else {
				throw new IOException("Cannot find bundle: " + bundleName);
			}
		}
		for (String bundleName : OPTIONAL_BUNDLES) {
			Bundle bundle = Platform.getBundle(bundleName);
			if (bundle != null) {
				classPathBundles.add(bundle);
			}
		}
					
		String pathSeparator = System.getProperty("path.separator"); //$NON-NLS-1$
		StringBuilder classPath = new StringBuilder();
		if (classPathBundles.size() > 0) {
			for (int i = 0; i < classPathBundles.size() - 1; i++) {
				classPath.append(getBundleLocation(classPathBundles.get(i)));
				classPath.append(pathSeparator);
			}
			classPath.append(getBundleLocation(classPathBundles.get(classPathBundles.size() - 1)));
		}
		
		return classPath.toString();
	}
	
	private static List<Bundle> getBundleAndFragments(String symbolicName) {
		List<Bundle> bundles = new ArrayList<Bundle>();
		Bundle bundle = Platform.getBundle(symbolicName);

		if (bundle != null) {
			bundles.add(bundle);
			
			if (bundle instanceof BundleHost) {
				BundleFragment[] fragments = ((BundleHost) bundle).getFragments();
				if (fragments != null) {
					Collections.addAll(bundles, fragments);
				}				
			}
		}		
		
		return bundles;
	}
	
	private static String getBundleLocation(Bundle bundle) throws IOException {
		try {
			File bundleLocation = FileLocator.getBundleFile(bundle);
			
			if (bundleLocation.isDirectory()) {
				File binDirectory = new File(bundleLocation, "bin"); //$NON-NLS-1$
				if (binDirectory.isDirectory()) {
					bundleLocation = binDirectory;
				}
			}
	
			return bundleLocation.getCanonicalPath();
		} catch (IOException e) {
			throw new IOException("Cannot resolve the path to bundle: " + bundle.getSymbolicName(), e);
		}
	}
}
