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

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.vpe.browsersim.browser.IBrowser;
import org.jboss.tools.vpe.browsersim.model.preferences.CommonPreferences;
import org.jboss.tools.vpe.browsersim.model.preferences.SpecificPreferences;
import org.jboss.tools.vpe.browsersim.ui.PreferencesWrapper;
import org.jboss.tools.vpe.browsersim.ui.menu.FileMenuCreator;

public class CordovaSimFileMenuCreator extends FileMenuCreator {
	@Override
	protected void viewServerSource(IBrowser browser) {
		String url = browser.getUrl();
		String port = Integer.toString(CordovaSimArgs.getPort());
		int index = url.indexOf(port) + port.length();
		url = url.substring(index);
		File sourceFile = new File(CordovaSimArgs.getRootFolder() + url.substring(0, minAnchorIndex(url)));
		if (sourceFile.exists()) {
			System.out.println(OPEN_FILE_COMMAND + sourceFile.getAbsolutePath()); // send command to Eclipse
		} else {
			super.viewServerSource(browser);
		}
	}
	
	private int minAnchorIndex(String url) {
		int questionMarkIndex = url.indexOf('?');
		questionMarkIndex = questionMarkIndex != -1 ? questionMarkIndex : url.length();
		
		int hashIndex =  url.indexOf('#');
		hashIndex = hashIndex != -1 ? hashIndex : url.length();
		return Math.min(questionMarkIndex, hashIndex);
	}
	
	@Override
	protected PreferencesWrapper openDialog(Shell parentShell, CommonPreferences commonPreferences,
			SpecificPreferences specificPreferences, String currentUrl) {
		return new CordovaSimManageDeviceDialog(parentShell, SWT.APPLICATION_MODAL
				| SWT.SHELL_TRIM, commonPreferences, specificPreferences, currentUrl).open();
	}
}
