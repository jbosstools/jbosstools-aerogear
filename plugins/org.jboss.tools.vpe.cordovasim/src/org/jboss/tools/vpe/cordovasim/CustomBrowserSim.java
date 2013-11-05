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
import org.jboss.tools.vpe.browsersim.browser.IBrowser;
import org.jboss.tools.vpe.browsersim.browser.PlatformUtil;
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
	private IBrowser inAppBrowser;
	private IBrowser rippleToolSuiteBrowser;

	public CustomBrowserSim(String homeUrl, Shell parentShell) {
		super(homeUrl, parentShell);
	}
	
	@Override
	protected ControlHandler createControlHandler(IBrowser browser, String homeUrl, SpecificPreferences specificPreferences) {
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
	@SuppressWarnings("nls")
	protected void setSelectedDevice(Boolean refreshRequired) {
		String currentOs = PlatformUtil.getOs();
		if (inAppBrowser != null && refreshRequired == null 
				&& (PlatformUtil.OS_LINUX.equals(currentOs) || PlatformUtil.OS_MACOSX.equals(currentOs))) {
			inAppBrowser.dispose(); // disposing inAppBrowser for Linux and Mac OS. Unfortunately this solution doesn't work on Windows
			this.inAppBrowser = null;
			rippleToolSuiteBrowser.execute("(function(){ripple('platform/cordova/3.0.0/bridge/inappbrowser').close();})()");
		}
		
		super.setSelectedDevice(refreshRequired);
		
		if (inAppBrowser != null && refreshRequired == null && PlatformUtil.OS_WIN32.equals(currentOs)) { 
			rippleToolSuiteBrowser.refresh(); // have to make a full refresh to prevent native error on Windows
			this.inAppBrowser = null;
		} 
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

	public IBrowser getInAppBrowser() {
		return inAppBrowser;
	}

	public void setInAppBrowser(IBrowser inAppBrowser) {
		this.inAppBrowser = inAppBrowser;
	}

	public IBrowser getRippleToolBarBrowser() {
		return rippleToolSuiteBrowser;
	}

	public void setRippleToolBarBrowser(IBrowser rippleToolBarBrowser) {
		this.rippleToolSuiteBrowser = rippleToolBarBrowser;
	}
}