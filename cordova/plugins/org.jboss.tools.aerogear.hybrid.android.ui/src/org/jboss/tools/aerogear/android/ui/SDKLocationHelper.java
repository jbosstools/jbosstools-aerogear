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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.jboss.tools.aerogear.hybrid.ui.internal.preferences.HybridToolsPreferences;
/**
 * Helper class for the Android SDK location. 
 * 
 * @author Gorkem Ercan
 *
 */
public class SDKLocationHelper {
	
	
	public static boolean defineSDKLocationIfNecessary(){
		if(isSDKLocationDefined())
			return true;

		Shell activeShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		boolean define = MessageDialog.openQuestion(activeShell, "Missing Android SDK", "In order to proceed,"
				+ " the location of the Android SDK must be defined. Define Now?");
		if(!define){
			return false;
		}
		
		PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(activeShell, 
				"org.jboss.tools.aerogear.hybrid.ui.internal.preferences.HybridMoblePreferencePage", 
				null, null);
		dialog.open();
		return defineSDKLocationIfNecessary();

	}
	
	public static String getSDKLocation()
	{
		if(isSDKLocationDefined())
			return  HybridToolsPreferences.getPrefs().getAndroidSDKLocation();
		return null;
	}

	public static boolean isSDKLocationDefined() {
		String sdkLocation = HybridToolsPreferences.getPrefs().getAndroidSDKLocation();
		return (sdkLocation != null && sdkLocation.length()>0);
	}

}
