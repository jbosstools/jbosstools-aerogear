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
package org.jboss.tools.vpe.cordovasim;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.vpe.browsersim.browser.BrowserSimBrowser;
import org.jboss.tools.vpe.browsersim.model.preferences.CommonPreferences;
import org.jboss.tools.vpe.browsersim.model.preferences.SpecificPreferences;
import org.jboss.tools.vpe.browsersim.model.preferences.SpecificPreferencesStorage;
import org.jboss.tools.vpe.browsersim.ui.BrowserSim;
import org.jboss.tools.vpe.browsersim.ui.ControlHandler;
import org.jboss.tools.vpe.browsersim.ui.menu.BrowserSimMenuCreator;
import org.jboss.tools.vpe.browsersim.ui.skin.BrowserSimSkin;
import org.jboss.tools.vpe.cordovasim.model.preferences.CordavaSimSpecificPreferencesStorage;
import org.jboss.tools.vpe.cordovasim.model.preferences.CordovaSimSpecificPreferences;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public class CustomBrowserSim extends BrowserSim {

	public CustomBrowserSim(String homeUrl, Shell parentShell) {
		super(homeUrl, parentShell);
	}
	
	@Override
	protected ControlHandler createControlHandler(BrowserSimBrowser browser, String homeUrl, SpecificPreferences specificPreferences) {
		return new CordovaSimControlHandler(browser, homeUrl, specificPreferences);
	}
	
	@Override
	protected BrowserSimMenuCreator createMenuCreator(BrowserSimSkin skin, CommonPreferences commonPreferences, SpecificPreferences specificPreferences, ControlHandler controlHandler, String homeUrl) {
		return new CordovaSimMenuCreator(skin, commonPreferences, specificPreferences, controlHandler, homeUrl);
	}
	
	@Override
	protected SpecificPreferencesStorage getSpecificPreferencesStorage() {
		return CordavaSimSpecificPreferencesStorage.INSTANCE;
	}

	public CordovaSimSpecificPreferences getSpecificPreferences() {
		return (CordovaSimSpecificPreferences)super.getSpecificPreferences();
	}
	
	@Override
	protected LocationListener createNavButtonsListener() {
		return new LocationAdapter() {
			public void changed(LocationEvent event) {
				if (event.top) {
					skin.locationChanged(event.location, true, true);
				}
			}
		};
	}
}