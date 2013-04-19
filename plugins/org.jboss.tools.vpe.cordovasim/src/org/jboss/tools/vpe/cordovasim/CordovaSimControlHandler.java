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
import org.eclipse.swt.browser.Browser;
import org.jboss.tools.vpe.browsersim.model.preferences.SpecificPreferences;
import org.jboss.tools.vpe.browsersim.ui.BrowserSimControlHandler;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public class CordovaSimControlHandler extends BrowserSimControlHandler {

	public CordovaSimControlHandler(Browser browser, String homeUrl, SpecificPreferences specificPreferences) {
		super(browser, homeUrl, specificPreferences);
	}

	@Override
	public void goBack() {
		boolean backButtonProcessed = (Boolean) browser.evaluate("return !!window.opener.bsBackbuttonPressed && !window.opener.bsBackbuttonPressed()");
		if (!backButtonProcessed) {
			super.goBack(); 
		}
	}
}
