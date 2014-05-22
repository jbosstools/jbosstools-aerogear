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
package org.jboss.tools.vpe.cordovasim.eclipse;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.jboss.tools.usage.event.UsageEventType;
import org.jboss.tools.usage.event.UsageReporter;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author "Yahor Radtsevich (yradtsevich)"
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.jboss.tools.vpe.cordovasim.eclipse"; //$NON-NLS-1$

	private static final String CORDOVASIM_ACTION = "cordovasim";
	private static final String USAGE_COMPONENT_NAME = "aerogear";

	// The shared instance
	private static Activator plugin;

	private UsageEventType launchEventType;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		launchEventType = new UsageEventType(USAGE_COMPONENT_NAME, UsageEventType.getVersion(this), null, CORDOVASIM_ACTION, Messages.UsageEventTypeLaunchLabelDescription, UsageEventType.HOW_MANY_TIMES_VALUE_DESCRIPTION);
		UsageReporter.getInstance().registerEvent(launchEventType);
	}

	public void countLaunchEvent() {
		UsageReporter.getInstance().countEvent(launchEventType.event(UsageEventType.OPEN_ACTION));
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	
	public static void logError(String message, Throwable throwable) {
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, message, throwable));
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
