/*******************************************************************************
 * Copyright (c) 2007-2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.vpe.cordovasim.eclipse;

import java.net.URL;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.browser.BrowserFactory;
import org.eclipse.ui.browser.IWebBrowser;
import org.jboss.tools.vpe.cordovasim.eclipse.util.CordovaSimLauncher;

/**
 * Contribution to the {@code org.eclipse.ui.editors} extension point
 * 
 * @author "Yahor Radtsevich (yradtsevich)"
 */
public class CordovaSimFactory extends BrowserFactory {
	@Override
	public IWebBrowser createBrowser(final String id, String location,
			String parameters) {
		return new IWebBrowser() {
			@Override
			public void openURL(URL url) throws PartInitException {
				CordovaSimLauncher.launchCordovaSim(url.toString());	
			}
			@Override
			public String getId() {
				return id;
			}
			@Override
			public boolean close() {
				return false;
			}
		};
	}

}
