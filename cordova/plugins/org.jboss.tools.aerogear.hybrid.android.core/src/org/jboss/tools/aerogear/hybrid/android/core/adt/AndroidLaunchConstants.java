/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.aerogear.hybrid.android.core.adt;

import org.jboss.tools.aerogear.hybrid.android.core.AndroidCore;

public interface AndroidLaunchConstants {
	
	public static final String ID_LAUNCH_CONFIG_TYPE = "org.jboss.tools.aerogear.hybrid.android.core.AndroidLaunchConfigurationType";
	
	/**
	 * Attribute for the name of the AVD to be used for the launch
	 */
	public static final String ATTR_AVD_NAME = AndroidCore.PLUGIN_ID +".ATTR_AVD_NAME";
	
	/**
	 * Attribute for the serial name for the device to be run 
	 */
	public static final String ATTR_DEVICE_SERIAL = AndroidCore.PLUGIN_ID+".ATTR_DEVICE_SERIAL";
	
	/**
	 * Attribute for filter passed to the logcat during launch
	 */
	public static final String ATTR_LOGCAT_FILTER =  AndroidCore.PLUGIN_ID + ".ATTR_LOGCAT_FILTER";
	
	/**
	 * The default value for the logcat filter
	 */
	public static final String VAL_DEFAULT_LOGCAT_FILTER = "Cordova:V DroidGap:V CordovaLog:V CordovaPlugin:V CordovaChromeClient:V CordovaWebView:V *:S";

}
