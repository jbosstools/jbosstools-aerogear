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
import org.jboss.tools.vpe.browsersim.browser.IBrowser;
import org.jboss.tools.vpe.browsersim.model.preferences.SpecificPreferences;
import org.jboss.tools.vpe.browsersim.ui.BrowserSimControlHandler;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public class CordovaSimControlHandler extends BrowserSimControlHandler {

	public CordovaSimControlHandler(IBrowser browser, String homeUrl, SpecificPreferences specificPreferences) {
		super(browser, homeUrl, specificPreferences);
	}

	@Override
	public void goBack() {
		if (!isBackButtonProcessed()) {
			super.goBack(); 
		}
	}
	
	@Override
	public void goHome() { 
		if (!isHomeButtonProcessed()) {
			super.goHome(); 
		}
	}
	
	@Override
	public void goForward() {
		if (!needToProcessInAppBrowserEvents()) {
			super.goForward();
		}
	}
	
	@Override
	public void refresh() {
		if(!needToProcessInAppBrowserEvents()) {
		   super.refresh();
		}
	}
	
	
	/**
	 * @return {@link Boolean} that indicates whether device's backButton was overridden via PhoneGap's Event API 
	 */
	@SuppressWarnings("nls")
	private boolean isBackButtonProcessed() { 
		return (Boolean) browser.evaluate("return !!window.opener.bsBackbuttonPressed && !window.opener.bsBackbuttonPressed()");
	}
	
	/**
	 * This method will return true only when inAppBrowser is shown. 
	 * Moreover it's impossible to override homeButton for Android 4.0. 
	 * Pressing home button when inAppBrowser is shown will simply close it (just like back button) 
	 * 
	 * @return {@link Boolean} that indicates whether device's homeButton was overridden 
	 */
	private boolean isHomeButtonProcessed() { 
		if (needToProcessInAppBrowserEvents()) {
			return isBackButtonProcessed();
		} 
		return false;
	}
	
	@SuppressWarnings("nls")
	private boolean needToProcessInAppBrowserEvents() {
		return (Boolean) browser.evaluate("return !!window.opener.needToProcessInAppBrowserEvents");
	}
}
