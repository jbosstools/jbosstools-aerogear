/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.vpe.cordovasim.model.preferences;

import org.jboss.tools.vpe.browsersim.browser.IBrowser;
import org.jboss.tools.vpe.browsersim.browser.IBrowserFunction;

/**
 * @author Ilya Buziuk (ibuziuk) 
 */
public class InitRipplePreferences implements IBrowserFunction {
	private IBrowser browser;
	private CordovaSimSpecificPreferences sp;
	
	public InitRipplePreferences(IBrowser browser, CordovaSimSpecificPreferences sp ) {
		this.browser = browser;
		this.sp = sp;
	}

	@Override
	public Object function(Object[] arguments) {
		String ripplePreferences = sp.getRipplePreferences();		
		if (ripplePreferences != null) {
			initPreferences(ripplePreferences);
		}
		return null;
	}
	
	private void initPreferences(String ripplePreferences) {
		if (browser != null && !browser.isDisposed()) {
			browser.execute("(function() {" //$NON-NLS-1$
					             + "var defaultValues = " + ripplePreferences + ";" //$NON-NLS-1$ //$NON-NLS-2$
					             + "window.localStorage2.setItem('ripple', JSON.stringify(defaultValues));" //$NON-NLS-1$
				          + "})();"); //$NON-NLS-1$
		}
	}
	
}
