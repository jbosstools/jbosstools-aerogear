/*******************************************************************************
 * Copyright (c) 2007-2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.vpe.cordovasim.events;

import javafx.application.Platform;

import org.jboss.tools.vpe.browsersim.browser.IBrowser;
import org.jboss.tools.vpe.browsersim.browser.javafx.JavaFXBrowser;
import org.jboss.tools.vpe.browsersim.util.BrowserSimUtil;
import org.jboss.tools.vpe.cordovasim.CordovaSimArgs;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;

/**
 * @author Yahor Radtsevich (yradtsevich)
 * @author Ilya Buziuk (ibuziuk)
 */
public class RippleInjector extends LocationAdapter {
	@Override
	public void changed(LocationEvent event) {
		final IBrowser browser = (IBrowser) event.widget;
		if (event.top) {
			if (browser instanceof JavaFXBrowser && BrowserSimUtil.isJavaFx8Available()) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						inject(browser);
						
					}
				});
			} else {
				inject(browser);
			}
		}
	}
	
	private void inject(IBrowser browser) {
		browser.execute(
				/* We have to remember userAgent of the BrowserSim, cause window.navigator object would be overridden by ripple
				 * (see define function of the 'platform/w3c/1.0/navigator' in ripple.js and JBIDE-14652) */
				"window.bsUserAgent = window.navigator.userAgent;" +  //$NON-NLS-1$
				"window.opener.document.getElementById('userAgentInfo').innerHTML = bsUserAgent;" +  //$NON-NLS-1$
				"window.opener.document.getElementById('cordova-version-container').innerHTML = '" + CordovaSimArgs.getCordovaVersion() +"';" +  //$NON-NLS-1$ //$NON-NLS-2$
				/* Cordova's InAppBrowser API overrides window.open function, so we have to remember it for FireBug Lite
				 * (see FireBugLiteLoader.java and JBIDE-14625) */
				"window._bsOriginalWindowOpen = window._bsOriginalWindowOpen || window.open;" +  //$NON-NLS-1$
				"if (window.opener.ripple) {" + //$NON-NLS-1$
					"window.opener.ripple('bootstrap').inject(window, document);" +  //$NON-NLS-1$
				"}");  //$NON-NLS-1$
		browser.forceFocus();
	}
}
