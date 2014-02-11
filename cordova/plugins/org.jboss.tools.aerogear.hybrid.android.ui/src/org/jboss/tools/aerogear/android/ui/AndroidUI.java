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
package org.jboss.tools.aerogear.android.ui;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.jboss.tools.aerogear.hybrid.android.ui.internal.preferences.AndroidPreferences;
import org.jboss.tools.aerogear.hybrid.ui.HybridUI;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class AndroidUI extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.jboss.tools.aerogear.hybrid.android.ui"; //$NON-NLS-1$

	// The shared instance
	private static AndroidUI plugin;
	
	/**
	 * The constructor
	 */
	public AndroidUI() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		//We use the HybridUIs pref store as we do not have many preferences.
		IPreferenceStore store = HybridUI.getDefault().getPreferenceStore();
		AndroidPreferences.init(store);
		store.addPropertyChangeListener(new IPropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				AndroidPreferences.getPrefs().loadValues(event);
				
			}
		});
		AndroidPreferences.getPrefs().loadValues();
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
	public static AndroidUI getDefault() {
		return plugin;
	}

}
