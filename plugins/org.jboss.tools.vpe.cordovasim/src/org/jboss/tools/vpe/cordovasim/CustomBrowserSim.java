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
import org.jboss.tools.vpe.browsersim.browser.BrowserSimBrowser;
import org.jboss.tools.vpe.browsersim.model.preferences.SpecificPreferences;
import org.jboss.tools.vpe.browsersim.ui.BrowserSim;
import org.jboss.tools.vpe.browsersim.ui.ControlHandler;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public class CustomBrowserSim extends BrowserSim {

	public CustomBrowserSim(String homeUrl) {
		super(homeUrl);
	}

	@Override
	protected ControlHandler createControlHandler(BrowserSimBrowser browser, String homeUrl, SpecificPreferences specificPreferences) {
		return new CordovaSimControlHandler(browser, homeUrl, specificPreferences);
	}
}