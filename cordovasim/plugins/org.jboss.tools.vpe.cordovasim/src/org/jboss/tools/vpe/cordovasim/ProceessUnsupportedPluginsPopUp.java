/*******************************************************************************
 *  Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.vpe.cordovasim;

import org.jboss.tools.vpe.browsersim.browser.IBrowserFunction;
import org.jboss.tools.vpe.cordovasim.model.preferences.CordovaSimSpecificPreferences;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public class ProceessUnsupportedPluginsPopUp implements IBrowserFunction {
	CustomBrowserSim customBrowserSim;
	
	public ProceessUnsupportedPluginsPopUp(CustomBrowserSim customBrowserSim) {
		this.customBrowserSim = customBrowserSim;
	}
	
	// JBIDE-17588 CordovaSim: Need to replace "I Haz CheeseBurger" pop-up
	@Override
	public Object function(Object[] arguments) {
		CordovaSimSpecificPreferences specificPreferences = customBrowserSim.getSpecificPreferences();
		if (arguments != null && arguments.length > 0) {
			boolean isChecked = (Boolean) arguments[0];
			specificPreferences.setShowUnsupportedPluginsPopUp(!isChecked);
		}
		return specificPreferences.showUnsupportedPluginsPopUp();
	}

}
