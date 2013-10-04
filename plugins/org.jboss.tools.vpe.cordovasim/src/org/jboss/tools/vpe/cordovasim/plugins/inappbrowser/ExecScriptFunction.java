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
package org.jboss.tools.vpe.cordovasim.plugins.inappbrowser;


import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.jboss.tools.vpe.cordovasim.plugins.inappbrowser.exception.ExecScriptException;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public class ExecScriptFunction extends BrowserFunction {
	private Browser inAppBrowser;
	
	public ExecScriptFunction(Browser browser, Browser inAppBrowser, String name) {
		super(browser, name);
		this.inAppBrowser = inAppBrowser;
	}
	
	
	@Override
	public Object function(Object[] arguments) {
		String code = null;
		String success = null;
		String fail = null;
		
		if (arguments != null) {
			if (arguments[0] != null) {
				code = arguments[0].toString();
			}
			if (arguments[1] != null) {
				success = arguments[1].toString();
			}
			if (arguments[2] != null) {
				fail = arguments[2].toString();
			}

			if (code != null) {
				inAppBrowser.execute(code);
				if (success != null) {
					getBrowser().execute("(" + success + "())");
				} 
			} else { //XXX not really good
				getBrowser().execute("(" + fail + "())");
			}		
		}
		
		return null;
	}
	
}