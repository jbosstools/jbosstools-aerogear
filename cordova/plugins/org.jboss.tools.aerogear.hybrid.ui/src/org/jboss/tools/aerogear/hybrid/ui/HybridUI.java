/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *       Red Hat, Inc. - initial API and implementation
 *******************************************************************************/
package org.jboss.tools.aerogear.hybrid.ui;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class HybridUI extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.jboss.tools.aerogear.hybrid.ui"; //$NON-NLS-1$

	// The shared instance
	private static HybridUI plugin;
	
	/**
	 * The constructor
	 */
	public HybridUI() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
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
	public static HybridUI getDefault() {
		return plugin;
	}
	
	/**
     * Returns an image descriptor for the icon referenced by the given path
     * and contributor plugin
     * 
     * @param plugin symbolic name
     * @param path the path of the icon 
     * @return image descriptor or null
     */
    public static ImageDescriptor getImageDescriptor(String name, String path) {
		Bundle bundle = Platform.getBundle(name);
		if (path != null) {
			URL iconURL = FileLocator.find(bundle , new Path(path), null);
			if (iconURL != null) {
				return ImageDescriptor.createFromURL(iconURL);
			}
		}
		return null;
    }
}
