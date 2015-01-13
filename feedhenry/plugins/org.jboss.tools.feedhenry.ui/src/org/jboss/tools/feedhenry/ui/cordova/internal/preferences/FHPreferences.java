/*******************************************************************************
 * Copyright (c) 2014,2015 Red Hat, Inc. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * 	Contributors:
 * 		 Red Hat Inc. - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.jboss.tools.feedhenry.ui.cordova.internal.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.jboss.tools.feedhenry.ui.FHPlugin;
/**
 * FeedHenry preferences 
 * 
 * @author Gorkem Ercan
 *
 */
public class FHPreferences implements IPropertyChangeListener{
	
	private static FHPreferences instance;
	private IPreferenceStore kernelStore;
	private String fhURL;
	private String userAPIKey;
	
	private FHPreferences(IPreferenceStore store){
		this.kernelStore = store;
	}
	
	/**
	 * Creates and returns the single instance
	 * @return
	 */
	public static final FHPreferences getPreferences(){
		synchronized (FHPreferences.class) {
			if(instance == null ){
				IPreferenceStore prefs = FHPlugin.getDefault().getPreferenceStore();
				FHPreferences preferences = new FHPreferences(prefs);
				prefs.addPropertyChangeListener(preferences);
				preferences.loadValues();
				instance = preferences;
			}
		}
		return instance;
	}

	private void loadValues() {
		fhURL = kernelStore.getString(FHPreferenceConstants.PREF_TARGET_URL);
		userAPIKey = kernelStore.getString(FHPreferenceConstants.PREF_API_KEY);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if(event.getProperty().equals(FHPreferenceConstants.PREF_API_KEY) || 
				event.getProperty().equals(FHPreferenceConstants.PREF_TARGET_URL)){
			loadValues();
		}
	}

	/**
	 * Configured url on the preferences for FeedHenry target
	 * @return
	 */
	public String getFeedHenryURL() {
		return fhURL;
	}
	
	/**
	 * API key to use for authentication.
	 * @return
	 */
	public String getUserAPIKey() {
		return userAPIKey;
	}

}
