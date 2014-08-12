/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.aerogear.thym;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.jboss.tools.aerogear.thym.internal.convert.NatureConvertListener;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class ThymPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.jboss.tools.aerogear.thym"; //$NON-NLS-1$
	// The shared instance
	private static ThymPlugin plugin;
	private static ILog logger;
	
	
	
	/**
	 * The constructor
	 */
	public ThymPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		logger = Platform.getLog(this.getBundle());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		if(NatureConvertListener.getInstance() != null){
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(NatureConvertListener.getInstance());
		}
		super.stop(context);
		
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static ThymPlugin getDefault() {
		return plugin;
	}
	/**
	 * Log to bundle's log.
	 * 
	 * @param status
	 * @param message
	 * @param throwable
	 */
	public static void log(int status, String message, Throwable throwable ){
		logger.log(new Status(status, message, PLUGIN_ID,throwable));
	}
}
