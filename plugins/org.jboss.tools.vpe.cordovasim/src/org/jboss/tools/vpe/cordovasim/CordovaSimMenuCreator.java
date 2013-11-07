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

import org.eclipse.swt.widgets.Menu;
import org.jboss.tools.vpe.browsersim.model.preferences.CommonPreferences;
import org.jboss.tools.vpe.browsersim.model.preferences.SpecificPreferences;
import org.jboss.tools.vpe.browsersim.ui.ControlHandler;
import org.jboss.tools.vpe.browsersim.ui.menu.BrowserSimMenuCreator;
import org.jboss.tools.vpe.browsersim.ui.menu.ToolsMenuCreator;
import org.jboss.tools.vpe.browsersim.ui.skin.BrowserSimSkin;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public class CordovaSimMenuCreator extends BrowserSimMenuCreator {
	public CordovaSimMenuCreator(BrowserSimSkin skin, CommonPreferences cp, SpecificPreferences sp, ControlHandler controlHandler, String homeUrl) {
		super(skin, cp, sp, controlHandler, homeUrl);
	}
	
	@Override
	protected void addToolsItems(Menu contextMenu, BrowserSimSkin skin, CommonPreferences commonPreferences, SpecificPreferences specificPreferences, String homeUrl) {
		ToolsMenuCreator.addDebugItem(contextMenu, skin, commonPreferences.getWeinreScriptUrl(), commonPreferences.getWeinreClientUrl(), specificPreferences.isJavaFx());
		ToolsMenuCreator.addScreenshotMenuItem(contextMenu, skin, commonPreferences);
		ToolsMenuCreator.addLiveReloadItem(contextMenu, specificPreferences);
		ToolsMenuCreator.addTouchEventsItem(contextMenu, specificPreferences);
	}
	
	@Override
	protected void addFileItems(Menu menu, BrowserSimSkin skin,	CommonPreferences commonPreferences, SpecificPreferences specificPreferences) {
		new CordovaSimFileMenuCreator().addItems(menu, skin, commonPreferences, specificPreferences);	
	}
}
