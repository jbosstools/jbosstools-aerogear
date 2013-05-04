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
package org.jboss.tools.aerogear.hybrid.ui.internal.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.PropertyChangeEvent;
/**
 * Central location for all the preferences
 * 
 * @author Gorkem Ercan
 *
 */
public class HybridToolsPreferences {
	
	public static final String PREF_ANDROID_SDK_LOCATION = "Android_SDK_Loc";
	
	private static HybridToolsPreferences thePreferences = new HybridToolsPreferences();
	private IPreferenceStore kernelStore;
	private volatile String androdSDKLocation;
	
	public static void init(IPreferenceStore store){
		thePreferences.kernelStore = store;
	}
	
	public static HybridToolsPreferences getPrefs(){
		return thePreferences;
	}
	
	public void loadValues(){
		loadValues(null);
	}
	
	public void loadValues(PropertyChangeEvent event) {
		if(event == null || event.getProperty().equals(PREF_ANDROID_SDK_LOCATION)){
			androdSDKLocation = kernelStore.getString(PREF_ANDROID_SDK_LOCATION);			
		}
	}
	
	public String getAndroidSDKLocation(){
		return androdSDKLocation;
	}
	
}
